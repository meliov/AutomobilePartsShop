<template>
  <div class="order-details">
    <h1 class="tw-text-2xl tw-font-bold tw-mb-4">{{ t('Order Details') }}</h1>
    <div v-if="order">
      <p><strong>{{ t('id') }}:</strong> {{ order.id }}</p>
      <p><strong>{{ t('date') }}:</strong> {{ formatDate(order.date) }}</p>
      <p><strong>{{ t('total') }}:</strong> {{ formatPrice(order.total) }}</p>
      <p><strong>{{ t('trackingNumber') }}:</strong> {{ order.trackingNumber }}</p>
      <h2 class="tw-text-xl tw-font-semibold tw-mt-4">{{ t('Ordered Items:') }}</h2>
      <ul class="order-items">
        <li v-for="item in order.items" :key="item.id" class="order-item">
          <div class="item-details">
            <p class="item-name">{{ item.name }}</p>
            <p class="item-quantity">{{ t('quantity') }}: {{ item.quantity }}</p>
            <p class="item-price">{{ t('price') }}: {{ formatPrice(item.price) }}</p>
          </div>
        </li>
      </ul>
    </div>
    <div v-else>
      <p>{{ t('orderDetails.notFound') }}</p>
    </div>
    <QButton
      outline
      :label="t('Go Back')"
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  max-width: 600px;
  margin: 0 auto;
  background-color: var(--q-bg-page);
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  color: var(--q-text-primary);
  transition: background-color 0.3s, color 0.3s;
}

.order-items {
  list-style: none;
  padding: 0;
  width: 100%;
}

.order-item {
  display: flex;
  flex-direction: column;
  padding: 1rem;
  margin-bottom: 1rem;
  background-color: var(--q-bg-card);
  border: 1px solid var(--q-border);
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: background-color 0.3s, border-color 0.3s;
}

.item-details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.item-name {
  font-weight: bold;
  font-size: 1.1rem;
}

.item-quantity,
.item-price {
  font-size: 0.9rem;
  color: var(--q-text-secondary);
}
</style>
