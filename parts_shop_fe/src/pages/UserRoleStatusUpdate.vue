<template>
  <q-page padding class="tw-p-4">
    <div class="tw-container tw-max-w-screen-xl tw-mx-auto">
      <q-card flat bordered class="tw-max-w-screen-lg tw-mx-auto">
        <q-card-section>
          <div class="tw-text-2xl tw-mb-4 tw-font-serif">
            {{ $t('users.userManagement') }}
          </div>
          <q-separator class="q-my-md" />
          <q-table
            :rows="users"
            :columns="columns"
            row-key="id"
            class="tw-w-full"
          >
            <!-- eslint-disable-next-line  -->
            <template v-slot:body-cell-roles="props" >
              <q-select
                v-model="props.row.roles"
                :options="roles"
                multiple

                @update:model-value="updateUserStatusAndRole(props.row.id, props.row.status, props.row.roles)"
              />
            </template>
            <!-- eslint-disable-next-line  -->
            <template v-slot:body-cell-status="props">
              <q-select
                v-model="props.row.userStatus"
                :options="statuses"

                @update:model-value="updateUserStatusAndRole(props.row.id, props.row.status, props.row.roles)"
              />
            </template>
          </q-table>
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { api } from '@/boot/axios';
import { User} from "@/types";

const $q = useQuasar();
const API_BASE_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '');
const users = ref<User[]>([]);
const statuses = ref([
  { label: 'Active', value: 'ACTIVE' },
  { label: 'Banned', value: 'BANNED' },
]);
const roles = ref([
  { label: 'Admin', value: 'admin' },
  { label: 'User', value: 'user' },
]);

const columns = ref([
  { name: 'email', label: 'Email', field: 'email', align: 'left' },
  { name: 'roles', label: 'Roles', field: 'roles', align: 'left', sortable: true },
  { name: 'status', label: 'Status', field: 'userStatus', align: 'left', sortable: true },
] as {
  name: string;
  label: string;
  field: string | ((row: User) => unknown);
  required?: boolean;
  align?: "left" | "right" | "center";
  sortable?: boolean;
  sort?: (a: unknown, b: unknown, rowA: User, rowB: User) => number;
  format?: (val: number | string) => string;
  headerClasses?: string;
}[]);

const fetchUsers = async () => {
  try {
    const response = await api.get(`${API_BASE_URL}/user/all`);
    users.value = response.data as User[];
  } catch {
    $q.notify({
      color: 'negative',
      message: 'Failed to fetch users',
      icon: 'error',
    });
  }
};

const updateUserStatusAndRole = async (id: number, status: string | null, roles: string[] | null) => {
  try {
    const payload = {
      newStatus: status,
      roles: roles,
    };
    await api.put(`${API_BASE_URL}/user/change-status-role/${id}`, payload);
    $q.notify({
      color: 'positive',
      message: 'User status and role updated successfully',
      icon: 'check_circle',
    });
  } catch  {
    $q.notify({
      color: 'negative',
      message: 'Failed to update user status and role',
      icon: 'error',
    });
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style lang="scss" scoped>
.q-table {
  width: 100%;
}
</style>
