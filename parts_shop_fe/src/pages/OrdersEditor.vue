<template>
  <q-page class="q-pa-md">
    <q-card>
      <q-card-section>
        <div class="text-h6">Orders</div>
        <q-table
          :rows="orders"
          :columns="columns"
          row-key="id"
          flat
          bordered
          :filter="filter"
          style="height: 70vh; overflow-y: auto;"
        >
          <!-- eslint-disable-next-line  -->
          <template v-slot:top-right>
            <!-- eslint-disable-next-line  -->
            <q-input borderless dense debounce="300" v-model="filter" placeholder="Search">
              <!-- eslint-disable-next-line  -->
              <template v-slot:append>
                <q-icon name="search" />
              </template>
            </q-input>
          </template>
          <!-- eslint-disable-next-line  -->
          <template v-slot:body-cell-actions="props">
            <q-td>
              <q-btn
                label="Accept"
                color="positive"
                :disable="props.row.status !== 'PENDING'"
                @click="acceptOrder(props.row.id)"
              ><q-tooltip v-if="props.row.status !== 'PENDING'">Order status is not 'PENDING'</q-tooltip></q-btn>
              <q-btn
                label="Reject"
                color="negative"
                class="q-ml-sm"
                :disable="props.row.status !== 'PENDING'"
                @click="rejectOrder(props.row.id)"
              >
                <q-tooltip v-if="props.row.status !== 'PENDING'">Order status is not 'PENDING'</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { api } from '@/boot/axios';
import { QVueGlobals, useQuasar } from 'quasar';
import {OrderDetails, Product} from "@/types";
import {API_ORDER_GET} from "@/constants/routes";

const API_BASE_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '');
const $q = useQuasar() as QVueGlobals;

const orders = ref<OrderDetails[]>([]);
const filter = ref<string>('');

const columns = [
  { name: 'id', required: true, label: 'ID', align: 'left', field: 'id', sortable: true },
  {
    name: 'items',
    label: 'Items',
    align: 'left',
    field: 'items',
    sortable: false,
    style: 'width: 80vh; min-width: 80vh; white-space: normal; word-wrap: break-word;', // Set fixed width and wrap content
    format: (val: Array<Product>) =>
      val.map(item => `${item.name} (Qty: ${item.quantity}, Total: $${(item.price * item.quantity).toFixed(2)})`).join(', ')
  },
  { name: 'total', label: 'Total', align: 'right', field: 'total', sortable: true },
  { name: 'date', label: 'Date', align: 'left', field: 'date', sortable: true },
  { name: 'shippingAddress', label: 'Shipping Address', align: 'left', field: 'shippingAddress', sortable: false },
  { name: 'paymentMethod', label: 'Payment Method', align: 'left', field: 'paymentMethod', sortable: true },
  { name: 'trackingNumber', label: 'Tracking Number', align: 'left', field: 'trackingNumber', sortable: true },
  { name: 'status', label: 'Status', align: 'left', field: 'status', sortable: true },
  { name: 'actions', label: 'Actions', align: 'center', field: () => '' },
] as {
  name: string;
  label: string;
  field: string | ((row: OrderDetails) => unknown);
  required?: boolean;
  align?: "left" | "right" | "center";
  sortable?: boolean;
  sort?: (a: unknown, b: unknown, rowA: OrderDetails, rowB: OrderDetails) => number;
  format?: (val: number | string) => string;
  headerClasses?: string;
}[];

async function fetchOrders() {
  try {
    const response = await api.get(`${API_BASE_URL}${API_ORDER_GET}`);
    if (response.status >= 400) {
      $q.notify({ type: 'negative', message: 'Error fetching orders' });
    } else {
      orders.value = response.data;
      console.log('orders: ', orders.value)
    }
  } catch (error) {
    console.error('Error fetching orders:', error);
    $q.notify({ type: 'negative', message: 'Error fetching orders' });
  }
}

async function acceptOrder(orderId: number) {
  try {
    const response = await api.post(`${API_BASE_URL}/orders/accept/${orderId}`);
    if (response.status >= 400) {
      $q.notify({ type: 'negative', message: 'Error accepting order' });
    } else {
      $q.notify({ type: 'positive', message: 'Order accepted successfully' });
      await fetchOrders();
    }
  } catch (error) {
    console.error('Error accepting order:', error);
    $q.notify({ type: 'negative', message: 'Error accepting order' });
  }
}

async function rejectOrder(orderId: number) {
  try {
    const response = await api.post(`${API_BASE_URL}/orders/reject/${orderId}`);
    if (response.status >= 400) {
      $q.notify({ type: 'negative', message: 'Error rejecting order' });
    } else {
      $q.notify({ type: 'positive', message: 'Order rejected successfully' });
      await fetchOrders();
    }
  } catch (error) {
    console.error('Error rejecting order:', error);
    $q.notify({ type: 'negative', message: 'Error rejecting order' });
  }
}

onMounted(async () => {
  await fetchOrders();
});
</script>
