<template>
  <q-page padding class="!tw-pb-16 !tw-px-3 tw-flex tw-justify-center tw-items-center">
    <div class="tw-w-full sm:tw-max-w-72 tw-mx-auto">
      <q-card flat bordered class="!tw-w-full !tw-max-w-54 tw-p-4 !tw-bg-transparent">
        <h4 class="tw-text-3xl tw-text-center tw-mb-8 tw-font-semibold tw-font-serif">
          {{ $t('profile.title') }}
        </h4>
        <q-form greedy class="!tw-w-full" @submit.prevent="updateProfile">
          <q-card-section v-if="success">
            <div class="tw-w-full tw-flex tw-justify-center tw-items-center">
              {{ t('profile.successMessage') }}
            </div>
          </q-card-section>

          <q-card-section
            v-if="!success"
            class="tw-flex tw-flex-col tw-gap-4 tw-w-full !tw-p-0 tw-mb-4"
          >
            <q-input
              v-model="updatedName"
              :label="$t('profile.name')"
              type="text"
              dense
              lazy-rules
              :rules="[required]"
              class="tw-w-full"
            />
            <q-input
              v-model="updatedEmail"
              disable
              :label="$t('profile.email')"
              type="email"
              dense
              lazy-rules
              :rules="[required, emailRules]"
              class="tw-w-full"
            />
            <q-input
              v-model="updatedFirstName"
              :label="$t('profile.firstName')"
              type="text"
              dense
              lazy-rules
              :rules="[required]"
              class="tw-w-full"
            />
            <q-input
              v-model="updatedLastName"
              :label="$t('profile.lastName')"
              type="text"
              dense
              lazy-rules
              :rules="[required]"
              class="tw-w-full"
            />
            <q-input
              v-model="updatedAddress"
              :label="$t('profile.address')"
              type="text"
              dense
              lazy-rules
              :rules="[required]"
              class="tw-w-full"
            />
          </q-card-section>

          <div v-if="errorMessage" class="tw-text-red-500 tw-mb-4">{{ errorMessage }}</div>

          <div class="tw-flex tw-flex-col tw-gap-2">
            <QButton
              v-if="!success"
              type="submit"
              :label="$t('profile.update')"
              class="!tw-w-full !tw-py-2.5"
            />
            <QButton
              v-if="!success"
              secondary
              :label="$t('profile.cancel')"
              class="!tw-w-full !tw-py-2.5"
              @click="goBack"
            />
            <QButton
              v-if="success"
              secondary
              :label="$t('profile.goBack')"
              class="!tw-w-full !tw-py-2.5 q-mb-lg"
              @click="goBack"
            />
            <q-expansion-item
              v-if="!authStore.getUserFromStorage()?.roles?.includes('ADMIN')"
              v-model="isCardDetailsVisible"
              :label="$t('profile.cardDetails')"
              expand-separator
              class="q-mt-lg"
            >
              <CardDetailsForm  v-model:model-value="updatedCardDetails"/>
            </q-expansion-item>
          </div>
        </q-form>
      </q-card>
    </div>
  </q-page>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useQuasar } from 'quasar';
import QButton from '@/components/base/QButton.vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { AxiosError } from 'axios';
import CardDetailsForm from "@/components/card/CardDetailsForm.vue";
import {CardDetails} from "@/types";

const authStore = useAuthStore();
const $q = useQuasar();
const router = useRouter();
const { t } = useI18n();


const updatedName = ref(authStore.user?.username || '');
const updatedEmail = ref(authStore.user?.email || '');
const updatedFirstName = ref(authStore.user?.firstName || '');
const updatedLastName = ref(authStore.user?.lastName || '');
const updatedAddress = ref(authStore.user?.address || '');
const updatedCardDetails = ref(authStore.user?.cardDetails || {
    cardNumber: '',
    cardHolderName: '',
    expirationDate: '',
    cvv: '123',
},);
const errorMessage = ref<string | null>(null);
const success = ref(false);
const isCardDetailsVisible = ref(false);
const goBack = () => {
  router.back();
};

const updateCardDetails = async (cardDetails: CardDetails) => {
    try {
      const message = await authStore.updateUserCard({
        ...cardDetails
      }).then(() => {
        return "Card details updated successfully";
      });
      success.value = true;
      $q.notify({
        type: 'positive',
        message: message,
        position: 'top',
        timeout: 5000,
        icon: 'check',
      })
    }catch (error) {
      const axiosError = error as AxiosError<{ error: string }>;
      errorMessage.value =
        axiosError.response?.data?.error || 'Failed to update card details. Please try again.';
      $q.notify({
        type: 'negative',
        message: errorMessage.value,
        position: 'top',
        timeout: 5000,
        icon: 'error',
      });

  }
};


const updateProfile = async () => {
  try {
    const message = await authStore.updateProfile({
      id: authStore.user?.id,
      username: updatedName.value,
      email: updatedEmail.value,
      firstName: updatedFirstName.value,
      lastName: updatedLastName.value,
      address: updatedAddress.value,
    });

    $q.notify({
      type: 'positive',
      message: message,
      position: 'top',
      timeout: 5000,
      icon: 'check',
    });
    success.value = true;
  } catch (error) {
    const axiosError = error as AxiosError<{ error: string }>;
    errorMessage.value =
      axiosError.response?.data?.error || 'Failed to update profile. Please try again.';
    $q.notify({
      type: 'negative',
      message: errorMessage.value,
      position: 'top',
      timeout: 5000,
      icon: 'error',
    });
  }
};

const required = (val: string) => !!val || t('errors.validation.required');
const emailRules = (val: string) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(val) ? true : t('errors.validation.invalidEmail');
};

watch(() => updatedCardDetails.value, async () =>{
  console.log('updatedCardDetails', updatedCardDetails.value);
  await updateCardDetails(updatedCardDetails.value);
})

</script>


