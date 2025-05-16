import { defineStore } from 'pinia';
import { ref } from 'vue';
import { IOrderForm, OrderDetails } from '@/types';
import { storage } from '@/utils/storage';
import {useAuthStore} from "@/stores/auth";
import {api} from "@/boot/axios";
import {QVueGlobals, useQuasar} from "quasar";
import {API_ORDER_GET, API_ORDER_SAVE} from "@/constants/routes";

export const useOrderStore = defineStore('order', () => {
  const orderForm = ref<IOrderForm>({
    shipping: {
      firstName: '',
      lastName: '',
      email: '',
      address: '',
    },
    payment: {
      method: 'card',
      cardDetails: {
        cardNumber: '',
        cardHolderName: '',
        expirationDate: '',
        cvv: '',
      },
    },
  });

  const orderHistory = ref<OrderDetails[]>([]);
  const $q = useQuasar() as QVueGlobals;
  const setShippingForm = (shippingData: IOrderForm['shipping']) => {
    orderForm.value.shipping = shippingData;
    storeOrder();
  };

  const loadPayment = () => {
    const authStore = useAuthStore();
    if (authStore.getUserFromStorage()) {
      const user = authStore.getUserFromStorage();
      orderForm.value.payment = {
        method: 'card',
        cardDetails: {
          cardNumber: user.cardDetails?.cardNumber || '',
          cardHolderName: user.cardDetails?.cardHolderName || '',
          expirationDate: user.cardDetails?.expirationDate || '',
          cvv: user.cardDetails?.cvv || '',
        },
      };
    } else {
      orderForm.value.payment = {
        method: 'card',
        cardDetails: {
          cardNumber: '',
          cardHolderName: '',
          expirationDate: '',
          cvv: '',
        },
      };
    }
    storeOrder()

  }
  const setPaymentForm = (paymentData: IOrderForm['payment']) => {
    orderForm.value.payment = paymentData ?? {
      method: 'card',
      cardDetails: {
        cardNumber: '',
        expiry: '',
        cvv: '',
      },
    };
    storeOrder();
  };

  const loadOrder = () => {
    const storedOrder = storage.get('orderForm');
    if (storedOrder) {
      orderForm.value = storedOrder;
    }else{
      const authStore = useAuthStore();
      if (authStore.getUserFromStorage()) {
        const user = authStore.getUserFromStorage();
        orderForm.value.shipping = {
          firstName: user.firstName || '',
          lastName: user.lastName || '',
          email: user.email || '',
          address: user.address || '',
        };
      }
    }
  };

  const storeOrder = () => {
    storage.set('orderForm', orderForm.value, {
      version: '1.0',
    });
  };

  const clearOrderForm = () => {
    orderForm.value = {
      shipping: {
        firstName: '',
        lastName: '',
        email: '',
        address: '',
      },
      payment: {
        method: 'card',
        cardDetails: {
          cardNumber: '',
          cardHolderName: '',
          expirationDate: '',
          cvv: '',
        },
      },
    };
    storage.remove('orderForm');
  };

  const addOrderToHistory = (order: OrderDetails) => {
    orderHistory.value.push(order);
    storage.set('orderHistory', orderHistory.value, {
      version: '1.0',
    });
  };

  const loadOrderHistory = () => {
    const storedOrderHistory = storage.get('orderHistory');
    if (storedOrderHistory) {
      orderHistory.value = storedOrderHistory;
    }
  };


  const saveOrder = async (order: OrderDetails) => {
    try {
      const savedOrder = await api.post<Promise<OrderDetails>>(API_ORDER_SAVE, {
        items: order.items,
        total: order.total,
        shippingAddress: `${order.shippingAddress}`,
        paymentMethod: order.paymentMethod,
        trackingNumber: '',
      }).then(r => r.data);
      if (savedOrder) {
        $q.notify({ type: 'positive', message: 'Order saved successfully!' });
        addOrderToHistory(savedOrder); // Optionally add to local history
      } else {
        $q.notify({ type: 'negative', message: 'Failed to save the order.' });
      }
    } catch (error) {
      console.error('Error saving order:', error);
      $q.notify({ type: 'negative', message: 'An error occurred while saving the order.' });
    }
  };

  const fetchOrders = async () => {
    try {
      const response = await api.get(API_ORDER_GET);
      if (response.status <= 400) {
        orderHistory.value = response.data as OrderDetails[];
      } else {
        $q.notify({ type: 'negative', message: 'Failed to fetch orders.' });
      }
    } catch (error) {
      console.error('Error fetching orders:', error);
      $q.notify({ type: 'negative', message: 'An error occurred while fetching orders.' });
    }
  };
  return {
    orderForm,
    orderHistory,
    setShippingForm,
    setPaymentForm,
    loadOrder,
    clearOrderForm,
    storeOrder,
    addOrderToHistory,
    loadOrderHistory,
    loadPayment,
    saveOrder,
    fetchOrders,
  };
});
