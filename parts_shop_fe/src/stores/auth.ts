import {api} from '@/boot/axios';
import {
  API_CHANGE_PASSWORD_PATH,
  API_LOGIN_PATH,
  API_PASSWORD_RESET_CONFIRM_PATH,
  API_REGISTER_PATH,
  API_USER_PATH,
  CHECK_EMAIL_PATH,
} from '@/constants/routes';
import {storage} from '@/utils/storage';
import {defineStore} from 'pinia';
import {Ref, ref} from 'vue';
import {QVueGlobals, useQuasar} from "quasar";
import {CreateUserView} from "@/models/CreateUserView";
import {User} from "@/types";


export const useAuthStore = defineStore('auth', () => {
  const $q = useQuasar() as QVueGlobals;
  const USER_EXPIRATION = 3600; // 1 hour in seconds
  //user must remain reff since there are other places which rely on it that its reactive
  const user: Ref<User> = ref<User>(storage.get('user'));


  const isLoggedIn =  () => Boolean(storage.getItemData('accessToken'));
  const fetchUser = async () => {
    try {

      if (user.value) {
        return;
      }

      const response = await api.get(`${API_USER_PATH}/${(user.value as User | null)?.id}`);
      user.value = response.data as User;
      storage.set('user', user.value, {
        expiration: USER_EXPIRATION,
        version: '1.0',
      });
    } catch (error) {
      console.error('Failed to fetch user data:', error);
      throw error;
    }
  };
  const login = async (email: string, password: string) => {
    try {
      const response = await api.post(API_LOGIN_PATH, { email, password });
      user.value = response.data as User;
      storage.set('user', user.value, {
        expiration: USER_EXPIRATION,
        version: '1.0',
      });
      if (isLoggedIn()) {
        await fetchUser()
      } else {
        throw new Error('Failed to retrieve token');
      }
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };
  const register = async (name: string, email: string, password: string) => {

      const val = await api.get<boolean>(
        `${CHECK_EMAIL_PATH}/${email}`,
      ).then(response => response.data)

      if(val === true){
        $q.notify({
          color: 'negative',
          position: 'bottom',
          timeout: 3000,
          message: 'User with this email already exists!',
          icon: 'error',
        });
        throw new Error("User with this email already exists!")
      }else{
        try {
          const user: CreateUserView = {username: name, email: email, password: password} // can be done without it tho
          await api.post(API_REGISTER_PATH, user);
          $q.notify({
            color: 'positive',
            position: 'bottom',
            timeout: 7000,
            message: 'User was created successfully! Confirmation email was sent to your email.',
            icon: 'success',
          });
        }catch (e){
          console.error('Registration failed:', e);
          $q.notify({
            color: 'negative',
            position: 'bottom',
            timeout: 3000,
            message: 'Registration failed. Please try again.',
            icon: 'error',
          });
          throw e;
        }
      }
  };

  const logout = () => {
    storage.remove('accessToken');
    storage.remove('refreshToken');
    storage.remove('user');
  };
  const updateProfile = async (profileData: { username: string; email: string }) => {
    try {
      const response = await api.put(API_USER_PATH, profileData);
      const updatedUser = response.data.user;
      user.value = {
        ...user.value,
        username: updatedUser.username,
        email: updatedUser.email,
      };
      storage.set('user', {
        ...user.value,
        timestamp: Date.now(),
        version: '1.0',
        expiration: USER_EXPIRATION,
      });
      return response.data.message;
    } catch (error) {
      console.error('Failed to update profile:', error);
      throw error;
    }
  };
  const changePassword = async (oldPassword: string, newPassword: string) => {
    try {
      const response = await api.put(
        API_CHANGE_PASSWORD_PATH,
        { oldPassword, newPassword },
      );
      return response.data.message;
    } catch (error) {
      $q.notify({
        color: 'negative',
        position: 'top',
        timeout: 1000,
        message: 'Failed to change password.',
        icon: 'error',
      });
      throw error;
    }
  };

  const resetPassword = async (email: string | null, newPassword: string) => {
    try {
      const response = await api.post(API_PASSWORD_RESET_CONFIRM_PATH, {
        email,
        newPassword,
      });
      return response.data.message;
    } catch (error) {
      console.error('Failed to reset password:', error);
      throw error;
    }
  };

  return {
    login,
    register,
    logout,
    fetchUser,
    updateProfile,
    changePassword,
    resetPassword,
    isLoggedIn,
    user
  };
});
