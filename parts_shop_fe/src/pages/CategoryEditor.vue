<template>
  <q-page class="q-pa-md">
    <q-card>
      <q-card-section>
        <div class="text-h6">Categories</div>
        <q-list bordered>
          <q-item v-for="category in categories" :key="category.id" clickable @click="selectCategory(category)">
            <q-item-section>{{ category.name }}</q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <q-separator />

      <q-card-section v-if="selectedCategory">
        <q-input v-model="selectedCategory.name" label="Edit Category Name" />
        <q-btn class="q-mt-md" label="Save" color="primary" @click="saveCategory" />
        <q-btn class="q-mt-md" label="Save" color="primary" @click="deleteCategory" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import {Category} from "@/types";
import {api} from '@/boot/axios';
const API_BASE_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '');

const categories = ref<Category[]>([]);

async function fetchCategories() {
  try {
    const response = await api.get(`${API_BASE_URL}/categories/get-all`);
    categories.value = response.data;
  } catch (error) {
    console.error('Error fetching categories:', error);
  }
}

const selectedCategory = ref<Category|null>(null);

async function saveCategory() {
  try {
    if (selectedCategory.value) {
      if (selectedCategory.value.id) {
        // Update existing category
        await api.put(`${API_BASE_URL}/categories/update/${selectedCategory.value.id}`, selectedCategory.value);
      } else {
        // Create new category
        const response = await api.post(`${API_BASE_URL}/categories/create`, selectedCategory.value);
        categories.value.push(response.data);
      }
      await fetchCategories();
      selectedCategory.value = null;
    }
  } catch (error) {
    console.error('Error saving category:', error);
  }
}

async function deleteCategory() {
  try {
    if (selectedCategory.value?.id) {
      await api.delete(`${API_BASE_URL}/categories/delete/${selectedCategory.value.id}`);
      await fetchCategories();
      selectedCategory.value = null;
    }
  } catch (error) {
    console.error('Error deleting category:', error);
  }
}

function selectCategory(category: Category) {
  selectedCategory.value = { ...category }; // shallow copy
}

onMounted(async () =>{
 await fetchCategories()
})
</script>
