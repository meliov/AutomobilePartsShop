import { defineStore } from 'pinia';
import { ref } from 'vue';
import { IOrderForm, OrderDetails } from '@/types';
import { storage } from '@/utils/storage';
import {useAuthStore} from "@/stores/auth";

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
        expiry: '',
        cvv: '',
      },
    },
  });

  const orderHistory = ref<OrderDetails[]>([]);

  const setShippingForm = (shippingData: IOrderForm['shipping']) => {
    orderForm.value.shipping = shippingData;
    storeOrder();
  };

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
          expiry: '',
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
  };
});
