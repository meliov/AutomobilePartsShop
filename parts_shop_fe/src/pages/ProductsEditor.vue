<template>
  <q-page class="q-pa-md">
    <q-card>
      <q-card-section>
        <div class="text-h6">Products</div>
        <q-list bordered>
          <q-item v-for="product in products" :key="product.id" clickable @click="selectProduct(product)">
            <q-item-section>{{ product.name }}</q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <q-separator />

      <q-card-section v-if="selectedProduct">
        <q-input v-model="selectedProduct.name" label="Name" />
        <q-input v-model="selectedProduct.description" label="Description" />
        <q-input v-model.number="selectedProduct.price" label="Price" type="number" />
        <q-input v-model.number="selectedProduct.quantity" label="Quantity" type="number" />
        <q-select
          v-model="selectedProduct.category"
          :options="categories"
          option-label="name"
          option-value="id"
          emit-value
          map-options
          label="Category"
        />
        <q-btn class="q-mt-md" label="Save" color="primary" @click="saveProduct" />
        <q-btn class="q-mt-md" label="Delete" color="primary" @click="deleteProduct" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import {Category, Product} from "@/types";
import {api} from '@/boot/axios';

const API_BASE_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '');

const categories = ref<Category[]>([]);
const products = ref<Product[]>([]);
const selectedProduct = ref<Product | null>(null);

async function fetchCategories() {
  try {
    const response = await api.get(`${API_BASE_URL}/categories/get-all`);
    categories.value = response.data;
  } catch (error) {
    console.error('Error fetching categories:', error);
  }
}

async function fetchProducts() {
  try {
    const response = await api.get(`${API_BASE_URL}/products/get-all`);
    products.value = response.data; // Adjusted to match the paginated response structure
  } catch (error) {
    console.error('Error fetching products:', error);
  }
}

function selectProduct(product: Product) {
  selectedProduct.value = { ...product }; // shallow copy
}

async function saveProduct() {
  try {
    if (selectedProduct.value) {
      if (selectedProduct.value.id) {
        // Update existing product
        await api.put(`${API_BASE_URL}/products/update/${selectedProduct.value.id}`, selectedProduct.value);
      } else {
        // Create new product
        const response = await api.post(`${API_BASE_URL}/products/create`, selectedProduct.value);
        products.value.push(response.data);
      }
      await fetchProducts();
      selectedProduct.value = null;
    }
  } catch (error) {
    console.error('Error saving product:', error);
  }
}

async function deleteProduct() {
  try {
    await api.delete(`${API_BASE_URL}/products/delete/${selectedProduct.value?.id}`);
    await fetchProducts();
  } catch (error) {
    console.error('Error deleting product:', error);
  }
}

onMounted(async () => {
 await fetchCategories();
 await fetchProducts();
});
</script>
