<template>
  <q-page class="q-pa-md">
    <q-card>
      <q-card-section>
        <div class="text-h6">Products</div>
        <q-list bordered style="max-height: 35vh; width: 100%; overflow-y: auto;">
          <q-item v-for="product in products" :key="product.id!!" clickable @click="selectProduct(product)">
            <q-item-section>{{ product.name }}</q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <q-separator />
      <q-btn class="q-mt-md" icon="add" color="primary" @click="openProductDialog" />
      <q-card-section v-if="selectedProduct">
        <q-input v-model="selectedProduct.name" label="Name" />
        <q-input v-model="selectedProduct.description" label="Description" autogrow />
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
      <q-dialog v-model="isProductDialogOpen" >
        <q-card style="width: 35vh">
          <q-card-section>
            <div class="text-h6">Create Product</div>
          </q-card-section>
          <q-card-section>
            <q-input v-model="dialogProduct.name" label="Name" />
            <q-input v-model="dialogProduct.description" label="Description"  autogrow/>
            <q-input v-model.number="dialogProduct.price" label="Price" type="number" />
            <q-input v-model.number="dialogProduct.quantity" label="Quantity" type="number" />
            <q-select
              v-model="dialogProduct.category"
              :options="categories"
              option-label="name"
              option-value="id"
              emit-value
              map-options
              label="Category"
              autogrow
            />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="Cancel" color="negative" @click="closeProductDialog" />
            <q-btn flat label="Save" color="primary" @click="emitProduct" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </q-card>
  </q-page>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import {Category, CreateProduct, Product} from "@/types";
import {api} from '@/boot/axios';
import {QVueGlobals, useQuasar} from "quasar";

const API_BASE_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '');

// dialog start
const isProductDialogOpen = ref(false);
const dialogProduct = ref<CreateProduct>({
  name: '',
  description: '',
  price: 0,
  quantity: 0,
  category: null,
  image: '',
  title: '',
  discount: false,
  discountedPrice: 0,
  additionalImages: [],
  rating: null,
});

function openProductDialog() {
  dialogProduct.value = {
    name: '',
    description: '',
    price: 0,
    quantity: 0,
    category: null,
    image: '',
    title: '',
    discount: false,
    discountedPrice: 0,
    additionalImages: [],
    rating: null,
  };
  isProductDialogOpen.value = true;
}

function closeProductDialog() {
  isProductDialogOpen.value = false;
}

function emitProduct() {
  dialogProduct.value = { ...dialogProduct.value, category: categories.value.find(it => it.id === dialogProduct.value.category) || null };
  selectedProduct.value = null
  isProductDialogOpen.value = false;
  saveProduct();
}
//dialog end
const categories = ref<Category[]>([]);
const products = ref<Product[]>([]);
const selectedProduct = ref<Product | null>(null);
const $q = useQuasar() as QVueGlobals;
async function fetchCategories() {
  try {
    const response = await api.get(`${API_BASE_URL}/categories/get-all`);
    if (response.status >= 400) {
      $q.notify({ type: 'negative', message: 'Error fetching categories' });
    } else {
      categories.value = response.data;
      // $q.notify({ type: 'positive', message: 'Categories fetched successfully' });
    }
  } catch (error) {
    console.error('Error fetching categories:', error);
    $q.notify({ type: 'negative', message: 'Error fetching categories' });
  }
}
async function fetchProducts() {
  try {
    const response = await api.get(`${API_BASE_URL}/products/get-all`);
    if (response.status >= 400) {
      $q.notify({ type: 'negative', message: 'Error fetching products' });
    } else {
      products.value = response.data;
      // $q.notify({ type: 'positive', message: 'Products fetched successfully' });
    }
  } catch (error) {
    console.error('Error fetching products:', error);
    $q.notify({ type: 'negative', message: 'Error fetching products' });
  }
}
function selectProduct(product: Product) {
  selectedProduct.value = { ...product }; // shallow copy
}

async function saveProduct() {
  try {
      let response;
      if (selectedProduct.value?.id) {

        // Update existing product
        response = await api.put(`${API_BASE_URL}/products/update/${selectedProduct.value.id}`, {...selectedProduct.value, category: categories.value.find(it => it.id === (selectedProduct.value!.category?.id ? selectedProduct.value!.category?.id : selectedProduct.value!.category))});
      } else {
        // Create new product
        response = await api.post(`${API_BASE_URL}/products/create`, dialogProduct.value);
        products.value.push(response.data);
      }
      if (response.status >= 400) {
        $q.notify({ type: 'negative', message: 'Error saving product' });
      } else {
        await fetchProducts();
        selectedProduct.value = null;
        $q.notify({ type: 'positive', message: 'Product saved successfully' });
      }
  } catch (error) {
    console.error('Error saving product:', error);
    $q.notify({ type: 'negative', message: 'Error saving product' });
  }
}
async function deleteProduct() {
  try {
    const response = await api.delete(`${API_BASE_URL}/products/delete/${selectedProduct.value?.id}`);
    if (response.status >= 400) {
      $q.notify({ type: 'negative', message: 'Error deleting product' });
    } else {
      await fetchProducts();
      $q.notify({ type: 'positive', message: 'Product deleted successfully' });
    }
  } catch (error) {
    console.error('Error deleting product:', error);
    $q.notify({ type: 'negative', message: 'Error deleting product' });
  }
}
onMounted(async () => {
 await fetchCategories();
 await fetchProducts();
});
</script>
