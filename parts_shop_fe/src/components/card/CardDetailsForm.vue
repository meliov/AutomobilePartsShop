<template>
  <q-form greedy @submit="submit">
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
        v-model="cardDetails.cardHolderName"
        :label="$t('profile.cardHolderName')"
        :rules="[required, cardHolderNameRules]"
        dense
        lazy-rules
      />
    </div>
    <div class="col-6">
      <q-input
        v-model="cardDetails.expirationDate"
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
      :label="$t('profile.updateCard')"
      class="!tw-w-full !tw-py-2.5"
      color="primary"
    />
  </q-form>
</template>

<script lang="ts" setup>
import {ref} from 'vue';
import {useI18n} from 'vue-i18n';
import {CardDetails} from '@/types';

const props = defineProps({
  modelValue: {
    type: Object as () => CardDetails,
    required: true,
  },
});

const emit = defineEmits<{
  (event: 'update:modelValue', value: CardDetails): void;
}>();
const { t } = useI18n();


const cardDetails = ref({ ...props.modelValue });

const required = (val: string) => !!val || t('errors.validation.required');

const cardNumberRules = (val: string) =>
  /^\d{16}$/.test(val.replace(/\s+/g, '')) || t('errors.validation.invalidCardNumber');
const cardHolderNameRules = (val: string) =>
  /^[a-zA-Z\s]+$/.test(val) || t('errors.validation.invalidCardHolderName');

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
  // console.log('Card details submitted:', cardDetails.value);
  emit('update:modelValue', cardDetails.value);
};

</script>
