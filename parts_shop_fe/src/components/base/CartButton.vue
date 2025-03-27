<template>
  <q-btn
    flat
    :to="'/cart'"
    :text-color="textColor"
    icon="shopping_cart"
    size="md"
    class="cart-btn !tw-p-3"
  >
    <q-badge class="cart-badge" :class="{ 'blink-animation': isAnimating }">
      {{ totalItems }}
    </q-badge>
  </q-btn>
</template>

<script lang="ts" setup>

defineProps({
  buttonSize: {
    type: String,
    required: true,
  },
  totalItems: {
    type: Number,
    required: true,
  },
  isAnimating: {
    type: Boolean,
    default: false,
  },
  textColor: {
    type: String,
    required: true,
  },
});
</script>
