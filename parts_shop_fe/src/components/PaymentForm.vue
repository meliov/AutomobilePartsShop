<template>
  <q-form
    ref="paymentFormRef"
    greedy
    class="row q-col-gutter-md payment-form"
    @submit.prevent="validatePaymentForm"
  >
    <div class="col-12">
      <q-input
        v-model="cardRef.cardDetails.cardNumber"
        :label="$t('checkout.cardNumber')"
        mask="#### #### #### ####"
        autofocus
        :rules="[required, cardNumberRules]"
        @keyup="emitValues()"
      />
    </div>
    <div class="col-6">
      <q-input
        v-model="cardRef.cardDetails.expiry"
        label="MM/YY"
        mask="##/##"
        :rules="[required, expiryRules]"
        @keyup="emitValues()"
      />
    </div>
    <div class="col-6">
      <q-input
        v-model="cardRef.cardDetails.cvv"
        label="CVV"
        mask="###"
        :rules="[required, cvvRules]"
        @keyup="emitValues()"
      />
    </div>
  </q-form>
</template>

<script lang="ts" setup>
import {ref} from 'vue';
import {useI18n} from 'vue-i18n';
import {PaymentDetails, QFormInstance} from '@/types';

const props = defineProps({
  paymentForm: {
    type: Object as () => PaymentDetails,
    required: true,
  },
});
const paymentFormRef = ref<QFormInstance|null>(null);

const emit = defineEmits<{
  (event: 'payment-valid', value: boolean): void;
  (event: 'update:payment', value: PaymentDetails): void;
}>();

const { t } = useI18n();
const cardRef = ref({
  ...props.paymentForm,
  cardDetails: {
    cardNumber: props.paymentForm.cardDetails?.cardNumber || '',
    expiry: props.paymentForm.cardDetails?.expiry || '',
    cvv: props.paymentForm.cardDetails?.cvv || '',
  },
})

const emitValues = () =>{
  validatePaymentForm()
  emit('update:payment', cardRef.value)
}

const validatePaymentForm = () => {
  if (paymentFormRef.value) {
    paymentFormRef.value.validate()
      .then((success: boolean) => {
        emit('payment-valid', success);
      })
      .catch(() => {
        emit('payment-valid', false);
      });
  } else {
    emit('payment-valid', false);
  }
};

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
const cvvRules = (val: string) => {
  if (!/^\d{3}$/.test(val)) {
    return t('errors.validation.invalidCVV');
  }
  return true;
};
</script>
