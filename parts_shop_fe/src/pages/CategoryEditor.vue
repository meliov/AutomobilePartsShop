<template>
  <q-page class="q-pa-md">
    <q-card>
      <q-card-section>
        <div class="text-h6">Categories</div>
        <q-list bordered style="max-height: 35vh; width: 100%; overflow-y: auto;">
          <q-item v-for="category in categories" :key="category.id!!" clickable @click="selectCategory(category)">
            <q-item-section>{{ category.name }}</q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <q-separator />
      <q-btn class="q-mt-md" icon="add" color="primary" @click="openCategoryDialog" />
      <q-card-section v-if="selectedCategory" >
        <q-input v-model="selectedCategory.name" label="Edit Category Name" />
        <q-btn class="q-mt-md" label="Save" color="primary" @click="saveCategory" />
        <q-btn class="q-mt-md" label="Delete" color="primary" @click="deleteCategory" />
      </q-card-section>
      <q-dialog v-model="isCategoryDialogOpen">
        <q-card>
          <q-card-section>
            <div class="text-h6">Select or Create Category</div>
          </q-card-section>
          <q-card-section>
            <q-input v-model="dialogCategory.name" label="Category Name" />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="Cancel" color="negative" @click="closeCategoryDialog" />
            <q-btn flat label="Save" color="primary" @click="emitCategory" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </q-card>
  </q-page>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import {Category} from "@/types";
import {api} from '@/boot/axios';
import {QVueGlobals, useQuasar} from "quasar";
const API_BASE_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '');
const $q = useQuasar() as QVueGlobals;

const categories = ref<Category[]>([]);


// dialog start
const isCategoryDialogOpen = ref(false);
const dialogCategory = ref<Category>({ id: null, name: '' });

function openCategoryDialog() {
  dialogCategory.value = { id: null, name: '' };
  isCategoryDialogOpen.value = true;
}

function closeCategoryDialog() {
  isCategoryDialogOpen.value = false;
}

function emitCategory() {
  selectedCategory.value = { ...dialogCategory.value };
  isCategoryDialogOpen.value = false;
  saveCategory();
}
//dialog end
async function fetchCategories() {
  try {
    const response = await api.get(`${API_BASE_URL}/categories/get-all`);
    categories.value = response.data;
    // $q.notify({ type: 'positive', message: 'Categories fetched successfully!' });
  } catch (error) {
    console.error('Error fetching categories:', error);
    $q.notify({ type: 'negative', message: 'Failed to fetch categories.' });
  }
}
const selectedCategory = ref<Category|null>(null);

async function saveCategory() {
  try {
    if (selectedCategory.value) {
      if (selectedCategory.value.id) {
        // Update existing category
        await api.put(`${API_BASE_URL}/categories/update/${selectedCategory.value.id}`, selectedCategory.value);
        $q.notify({ type: 'positive', message: 'Category updated successfully!' });
      } else {
        // Create new category
        const response = await api.post(`${API_BASE_URL}/categories/create`, selectedCategory.value);
        categories.value.push(response.data);
        $q.notify({ type: 'positive', message: 'Category created successfully!' });
      }
      await fetchCategories();
      selectedCategory.value = null;
    }
  } catch (error) {
    console.error('Error saving category:', error);
    $q.notify({ type: 'negative', message: 'Failed to save category.' });
  }
}
async function deleteCategory() {
  try {
    if (selectedCategory.value?.id) {
      await api.delete(`${API_BASE_URL}/categories/delete/${selectedCategory.value.id}`);
      await fetchCategories();
      selectedCategory.value = null;
      $q.notify({ type: 'positive', message: 'Category deleted successfully!' });
    }
  } catch (error) {
    console.error('Error deleting category:', error);
    $q.notify({ type: 'negative', message: 'Failed to delete category.' });
  }
}
function selectCategory(category: Category) {
  selectedCategory.value = { ...category }; // shallow copy
}

onMounted(async () =>{
 await fetchCategories()
})
</script>
