<template>
  <div class="order-details">
    <h1 class="tw-text-2xl tw-font-bold tw-mb-4">{{ t('orderDetails.title') }}</h1>
    <div v-if="order">
      <p><strong>{{ t('orderDetails.id') }}:</strong> {{ order.id }}</p>
      <p><strong>{{ t('orderDetails.date') }}:</strong> {{ formatDate(order.date) }}</p>
      <p><strong>{{ t('orderDetails.total') }}:</strong> {{ formatPrice(order.total) }}</p>
      <h2 class="tw-text-xl tw-font-semibold tw-mt-4">{{ t('orderDetails.items') }}</h2>
      <ul>
        <li v-for="item in order.items" :key="item.id">
          {{ item.name }} - {{ formatPrice(item.price) }} x {{ item.quantity }}
        </li>
      </ul>
    </div>
    <div v-else>
      <p>{{ t('orderDetails.notFound') }}</p>
    </div>
    <QButton
      outline
      :label="t('orderDetails.back')"
      class="tw-mt-4"
      @click="goBack"
    />
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useOrderStore } from '@/stores/order';
import { formatPrice } from '@/utils/currency';
import { formatDate } from '@/utils/date';
import { useI18n } from 'vue-i18n';
import QButton from '@/components/base/QButton.vue';

const route = useRoute();
const router = useRouter();
const orderStore = useOrderStore();
const { t } = useI18n();

const orderId = Number(route.params.id);
const order = computed(() => orderStore.orderHistory.find((o) => o.id === orderId));

const goBack = () => {
  router.back();
};
</script>

<style scoped>
.order-details {
  padding: 1rem;
}
</style>
