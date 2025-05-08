
<template>
  <q-page padding class="!tw-pb-16 !tw-px-3 tw-flex tw-justify-center tw-items-center">
    <div class="tw-w-full sm:tw-max-w-72 tw-mx-auto">
      <q-card flat bordered class="!tw-w-full !tw-max-w-54 tw-p-4 !tw-bg-transparent">
        <h4 class="tw-text-3xl tw-text-center tw-mb-8 tw-font-semibold tw-font-serif">
          {{'Email Verification'}}
        </h4>
        <q-form @submit.prevent="handleSubmit">
          <q-input
            v-model="email"
            label="Insert your account email"
            type="email"
            lazy-rules
            :rules="[required, emailRules]"
          />
          <div class="q-mt-md">
            <q-btn type="submit" label="Submit" color="primary" />
          </div>
        </q-form>

    </q-card>
    </div>
  </q-page>
</template>
<script setup lang="ts">
import {ref} from "vue";
import {useI18n} from "vue-i18n";
import {CHECK_EMAIL_PATH_API, PASSWORD_CHANGE_PATH} from "@/constants/routes";
import {useRouter} from "vue-router";
import {api} from "@/boot/axios";
import {QVueGlobals, useQuasar} from "quasar";

const { t } = useI18n();
const emailRules = (val: string) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(val) ? true : t('errors.validation.invalidEmail');
};
const $q = useQuasar() as QVueGlobals;
const router = useRouter();
const required = (val: string) => !!val || t('errors.validation.required');

const email = ref('');

const handleSubmit = async () => {
  try {
    const exists = await api.get<boolean>(
      `${CHECK_EMAIL_PATH_API}/${email.value}`,
    ).then(response => response.data)

    if (exists) {
      await router.push({path: PASSWORD_CHANGE_PATH, query: {email: email.value}});
    } else {
      $q.notify({
        type: 'negative',
        message: t('errors.validation.emailNotFound'),
        position: 'top',
        timeout: 5000,
        icon: 'error',
      });
    }
  } catch {
    $q.notify({
      type: 'negative',
      message: t('errors.validation.serverError'),
      position: 'top',
      timeout: 5000,
      icon: 'error',
    });
  }
};
</script>
<style scoped lang="scss">
</style>
