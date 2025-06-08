<template>
  <q-page padding class="tw-p-4">
    <div class="tw-container tw-max-w-screen-xl tw-mx-auto">
      <q-card flat bordered class="tw-max-w-screen-lg tw-mx-auto">
        <q-card-section>
          <div class="tw-text-2xl tw-mb-4 tw-font-serif">
            {{ t('userManagement.title') }}
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
              <q-td :props="props">
                <q-select
                  v-model="props.row.roles"
                  :options="roles"
                  multiple
                  outlined
                  dense
                  @update:model-value="changesDone = true"
                />
              </q-td>
            </template>
            <!-- eslint-disable-next-line  -->
            <template v-slot:body-cell-status="props">
              <q-td :props="props">
                <q-select
                  v-model="props.row.userStatus"
                  :options="statuses"
                  outlined
                  dense
                  @update:model-value="changesDone = true"
                />
              </q-td>
            </template>
          </q-table>
          <q-btn v-if="changesDone" class="q-mt-md" :label="t('userManagement.save')" color="primary" @click="saveChanges" />
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
import {useAuthStore} from "@/stores/auth";
import {useI18n} from "vue-i18n";

const $q = useQuasar();
const API_BASE_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '');
const users = ref<User[]>([]);
const statuses = ref([
  'ACTIVE',
  'BANNED',
  'PENDING',
]);
const roles = ref([
  'ADMIN',
  'USER'
]);
const { t } = useI18n();
const userAtuh = useAuthStore();

const columns = ref([
  { name: 'email', label: t('userManagement.email'), field: 'email', align: 'left' },
  { name: 'roles', label: t('userManagement.roles'), field: 'roles', align: 'left', sortable: true },
  { name: 'status', label: t('userManagement.status'), field: 'userStatus', align: 'left', sortable: true },
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
    const response = await api.get(`${API_BASE_URL}/user/all/${userAtuh.getUserFromStorage()?.id}`);
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

    const payload = {
      newStatus: status,
      roles: roles,
    };
    await api.put(`${API_BASE_URL}/user/change-status-role/${id}`, payload);
};

const changesDone = ref( false);

const saveChanges = async () => {
  try {
    await Promise.all(users.value.map(user =>
      updateUserStatusAndRole(user.id, user.userStatus, user.roles)
    ));
    changesDone.value = false;
    $q.notify({
      color: 'positive',
      message: 'User status and role updated successfully',
      icon: 'check_circle',
    });
  } catch {
    $q.notify({
      color: 'negative',
      message: 'Failed to save changes',
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
