<template>
  <q-page class="q-pa-md">
    <q-card>
      <q-card-section>
        <div class="text-h6">Products</div>
        <q-input v-model="searchQuery" label="Search Products" outlined dense class="q-mb-md" />
        <q-list bordered style="max-height: 35vh; width: 100%; overflow-y: auto;">
          <q-item v-for="product in filteredProducts" :key="product.id!!" clickable @click="selectProduct(product)">
            <q-item-section class="col-1">{{ product.name }}</q-item-section>
            <div class="col-4"></div>
            <q-item-section class="col-2" avatar>
              <q-img :src="getImageUrl(product.image)" :alt="product.name || product.title" />
            </q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <q-separator />
      <q-btn class="q-mt-md" icon="add" color="primary" @click="openProductDialog" />
      <q-card-section v-if="selectedProduct">
        <div class="row" style="width: 100%">
          <div class="col-3">
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
         </div>
          <div class="col-9">
            <q-card-section >
              <div class="row" style="width: 100%">
                <div class="col-4">
                <div class="text-h6">Images</div>
                  <q-card>
                    <q-input v-model="selectedProduct.image" label="Main Image URL" />
                    <q-img :src="selectedProduct.image" alt="Main Image" style="max-width: 100%; max-height: 150px;" />
                  </q-card>
                </div>
                <div class="col-1"></div>
                <div class="col-7">
                  <div class="text-h6">Additional Images</div>
                  <div class="row"  style="max-height: 20vh; width: 100%; overflow-y: auto;">
                    <q-card v-for="(image, index) in selectedProduct.additionalImages" :key="index" class="col-4 q-mb-md">
                      <q-btn icon="delete" color="secondary" @click="selectedProduct.additionalImages?.splice(index, 1)"></q-btn>
                      <q-card-section>
                        <q-input v-model="selectedProduct.additionalImages![index]" label="Image URL" />
                        <q-img :src="selectedProduct.additionalImages![index]" alt="Main Image" style="max-width: 100%; max-height: 150px;" />
                      </q-card-section>
                    </q-card>
                  </div>
                  <q-btn flat icon="add" color="secondary" @click="selectedProduct!!.additionalImages!!.push('')" />
                </div>
              </div>

            </q-card-section>
          </div>
        </div>
        <div class="row" style="width: 100%">
          <div class="col-3"></div>
        <q-btn class="q-mt-md col-2" label="Save" color="primary" @click="saveProduct" />
          <div class="col-2"></div>
        <q-btn class="q-mt-md col-2" label="Delete" color="primary" @click="deleteProduct" />
          <div class="col-3"></div>
        </div>
      </q-card-section>
      <q-dialog v-model="isProductDialogOpen" >
        <q-card style="width: 35vh">
          <q-card-section>
            <div class="text-h6">Create Product</div>
          </q-card-section>
          <q-card-section>
            <q-input v-model="dialogProduct.name" label="Name" />
            <q-input v-model="dialogProduct.description" label="Description" autogrow />
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
          <q-card-section>
            <div class="text-h6">Images</div>
            <q-input v-model="dialogProduct.image" label="Main Image URL" />
            <q-img :src="dialogProduct.image" alt="Main Image" style="max-width: 100%; max-height: 150px;" />
            <div class="text-subtitle2 q-mt-md">Additional Images</div>
            <q-list bordered style="max-height: 20vh; overflow-y: auto;">
              <q-item v-for="(image, index) in dialogProduct.additionalImages" :key="index">
                <q-item-section>
                  <q-input v-model="dialogProduct.additionalImages![index]" label="Image URL" />
                  <q-img :src="dialogProduct.additionalImages![index]" alt="Main Image" style="max-width: 100%; max-height: 150px;" />
                </q-item-section>
                <q-item-section side>
                  <q-img :src="image" alt="Additional Image" style="max-width: 100px; max-height: 100px;" />
                </q-item-section>
              </q-item>
            </q-list>
            <q-btn flat label="Add Image" color="primary" @click="dialogProduct!!.additionalImages!!.push('')" />
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
import {computed, onMounted, ref} from 'vue';
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
  dialogProduct.value = { ...dialogProduct.value, category: categories.value.find(it => it.id === dialogProduct.value.category) || null ,
    additionalImages: dialogProduct.value?.additionalImages?.filter(img => img !== '') || []};
  selectedProduct.value = null
  isProductDialogOpen.value = false;
  saveProduct();
}
//dialog end
const categories = ref<Category[]>([]);
const products = ref<Product[]>([]);
const searchQuery = ref<string>('');
const filteredProducts = computed(() =>
  products.value.filter(product =>
    product.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
);
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
        response = await api.put(`${API_BASE_URL}/products/update/${selectedProduct.value.id}`, {...selectedProduct.value,
          category: categories.value.find(it => it.id === (selectedProduct.value!.category?.id ? selectedProduct.value!.category?.id : selectedProduct.value!.category)),
          additionalImages: selectedProduct.value?.additionalImages?.filter(img => img !== '') || [],
        });
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

const getImageUrl = (imagePath: string) => {
  if (!imagePath) return '';
  // If the imagePath is already a full URL, return it directly
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }
};

</script>
