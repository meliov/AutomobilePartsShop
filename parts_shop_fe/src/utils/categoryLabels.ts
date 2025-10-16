import {ref} from "vue";
import {Category} from "@/types";
import {QVueGlobals, useQuasar} from "quasar";
import {api} from "@/boot/axios";
import {useUserStore} from "@/stores/user";

export const categoriesConst = ref<Category[]>([]);
const API_BASE_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '');
const $q = useQuasar() as QVueGlobals;
export async function fetchCategoryObjects() {
  try {
    const response = await api.get(`${API_BASE_URL}/categories/get-all`);
    categoriesConst.value = response.data;
  } catch (error) {
    console.error('Error fetching categories:', error);
    $q.notify({ type: 'negative', message: 'Failed to fetch categories.' });
  }
}


const userStore = useUserStore();


const getCategoryLabel = (categoryName: string): string => {
  if (categoryName == 'all'){
    return 'all'
  }

  const category = categoriesConst.value.find(c => c.name.toLowerCase() === categoryName.toLowerCase());
  if (!category) return categoryName;
  switch (userStore.settings.language) {
    case 'bg-BG':
      return category.nameBg;
    case 'fr-FR':
      return category.nameFr;
    default:
      return category.name;
  }
};

export const formatCategoryLabel = (category: string) => {

  return getCategoryLabel(category);
};
