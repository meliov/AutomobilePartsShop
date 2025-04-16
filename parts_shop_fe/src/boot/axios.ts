import { defineBoot } from '#q-app/wrappers';
import axios, { type AxiosInstance, type InternalAxiosRequestConfig } from 'axios';
import type { App } from 'vue';
import { useRouter } from 'vue-router';

/**
 * DESCRIPTION OF THE WORKFLOW
 *
 *
 * Attach Access Token to All Requests
 *
 * Read accessToken from localStorage.
 *
 * Add it to the Authorization header as:
 *
 * bash
 * Copy
 * Edit
 * Authorization: Bearer ${accessToken}
 * Handle 401 (Unauthorized) Responses
 *
 * If a response returns 401 and it's not a token refresh request, then:
 *
 * Use the stored refreshToken.
 *
 * Send a request to import.meta.env.VITE_API_REFRESH_TOKEN_PATH with:
 *
 * bash
 * Copy
 * Edit
 * Authorization: Bearer ${refreshToken}
 * On success:
 *
 * Update accessToken and refreshToken in storage from headers:
 *
 * arduino
 * Copy
 * Edit
 * "Jwt-Token", "Jwt-Refresh-Token"
 * Retry the original request with the new access token.
 *
 * On failure:
 *
 * Redirect to Login.
 *
 * Handle Login Response
 *
 * If the login request returns Jwt-Token and Jwt-Refresh-Token in headers:
 *
 * Save both tokens to localStorage.
 *
 * If No Tokens Exist
 *
 * Redirect user to the Login Page.
 */

declare module 'vue' {
  interface ComponentCustomProperties {
    $axios: AxiosInstance;
    $api: AxiosInstance;
  }
}

const api = axios.create({ baseURL: import.meta.env.VITE_API_URL });

let isRefreshing = false;
let failedRequestsQueue: Array<(token: string) => void> = [];

export default defineBoot(({ app }: { app: App }) => {
  const router = useRouter();

  // Request interceptor to attach access token
  api.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const accessToken = localStorage.getItem('accessToken');
      if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // Response interceptor to handle token updates and refresh logic
  api.interceptors.response.use(
    (response) => {
      // Update tokens from response headers if present
      const newAccessToken = response.headers['jwt-token'];
      const newRefreshToken = response.headers['jwt-refresh-token'];
      if (newAccessToken && newRefreshToken) {
        localStorage.setItem('accessToken', newAccessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
      }
      return response;
    },
    async (error) => {
      const originalRequest = error.config;
      const refreshToken = localStorage.getItem('refreshToken');

      // Handle 401 errors
      if (error.response?.status === 401 && !originalRequest._retry) {
        if (isRefreshing) {
          return new Promise((resolve) => {
            failedRequestsQueue.push((newAccessToken: string) => {
              originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
              resolve(api(originalRequest));
            });
          });
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
          // Make refresh request with refresh token as Bearer
          const refreshResponse = await axios.post(
            `${import.meta.env.VITE_API_REFRESH_TOKEN_PATH}`,
            {},
            {
              headers: {
                Authorization: `Bearer ${refreshToken}`
              }
            }
          );

          // Update tokens from refresh response headers
          const newAccessToken = refreshResponse.headers['Jwt-Token'];
          const newRefreshToken = refreshResponse.headers['Jwt-Refresh-Token'];

          if (newAccessToken && newRefreshToken) {
            localStorage.setItem('accessToken', newAccessToken);
            localStorage.setItem('refreshToken', newRefreshToken);
          }

          // Update Authorization header for future requests
          api.defaults.headers.common.Authorization = `Bearer ${newAccessToken}`;

          // Retry queued requests
          failedRequestsQueue.forEach(cb => cb(newAccessToken));
          failedRequestsQueue = [];

          // Retry original request
          return api(originalRequest);
        } catch (refreshError) {
          // Clear tokens and redirect to login on refresh failure
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
          router.push('/login');
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      }

      // Redirect to login if unauthorized and no refresh token
      if (error.response?.status === 401 && !refreshToken) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        await router.push('/login');
      }

      return Promise.reject(error);
    }
  );


  if (!localStorage.getItem('accessToken') && !localStorage.getItem('refreshToken')) {
    // Handle initial state with no tokens, but basically just dont do anything
  }

  app.config.globalProperties.$axios = axios;
  app.config.globalProperties.$api = api;
});

export { api };
