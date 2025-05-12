<template>
  <q-form ref="cardFormRef" greedy  @submit.prevent="submit">
    <div class="col-12">
      <q-input
        v-model="cardDetails.cardNumber"
        :label="$t('profile.cardNumber')"
        mask="#### #### #### ####"
        :rules="[required, cardNumberRules]"
        dense
        lazy-rules
      />
    </div>
    <div class="col-6">
      <q-input
        v-model="cardDetails.expiry"
        :label="$t('profile.expiry')"
        mask="##/##"
        :rules="[required, expiryRules]"
        dense
        lazy-rules
      />
    </div>
    <div class="col-6">
      <q-input
        v-model="cardDetails.cvv"
        :label="$t('profile.cvv')"
        mask="###"
        :rules="[required, cvvRules]"
        dense
        lazy-rules
      />
    </div>
    <q-btn
      type="submit"
      :label="$t('profile.validateCardDetails')"
      class="tw-mt-4"
      color="primary"
    />
  </q-form>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { CardDetails } from '@/types';

const props = defineProps({
  modelValue: {
    type: Object as () => CardDetails,
    required: true,
  },
});

const emit = defineEmits(['update:modelValue', 'valid']);
const { t } = useI18n();

const cardFormRef = ref();

const cardDetails = ref({
  cardNumber: props.modelValue.cardNumber || '',
  expiry: props.modelValue.expiry || '',
  cvv: props.modelValue.cvv || '',
});

const required = (val: string) => !!val || t('errors.validation.required');

const cardNumberRules = (val: string) =>
  /^\d{16}$/.test(val.replace(/\s+/g, '')) || t('errors.validation.invalidCardNumber');

const expiryRules = (val: string) => {
  const [month, year] = val.split('/').map(Number);
  if (!month || !year) return t('errors.validation.invalidExpiryDate');
  const now = new Date();
  const currentMonth = now.getMonth() + 1;
  const currentYear = now.getFullYear() % 100;
  return (
    (month >= 1 &&
      month <= 12 &&
      year >= currentYear &&
      (year > currentYear || month >= currentMonth)) ||
    t('errors.validation.invalidExpiryDate')
  );
};

const cvvRules = (val: string) =>
  /^\d{3}$/.test(val) || t('errors.validation.invalidCVV');

const submit = () => {
  // Only called if form is valid
  emit('update:modelValue', cardDetails.value);
  emit('valid');
};
</script>
