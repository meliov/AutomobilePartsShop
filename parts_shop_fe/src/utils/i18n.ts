import type { CurrencyOption, LanguageOption } from '@/types';

export const STORAGE_LANGUAGE_KEY = 'language';

export const languages: LanguageOption[] = [
  { value: 'en-US', label: 'English' },
  { value: 'bg-BG', label: 'Bulgarian' },
  { value: 'fr-FR', label: 'French' },
];

export const currencies: CurrencyOption[] = [
  { value: 'USD', label: 'US Dollar' },
  { value: 'EUR', label: 'Euro' },
  { value: 'BGN', label: 'Bulgarian Lev' },
];

export const i18nConfig = {
  datetimeFormats: {
    'en-US': {
      short: {
        year: 'numeric' as const,
        month: 'short' as const,
        day: 'numeric' as const,
      },
    },
    'fr-FR': {
      short: {
        year: 'numeric' as const,
        month: 'short' as const,
        day: 'numeric' as const,
      },
    },
    'bg-BG': {
      short: {
        year: 'numeric' as const,
        month: 'short' as const,
        day: 'numeric' as const,
      },
    },
  },
  numberFormats: {
    'en-US': {
      currency: {
        style: 'currency' as const,
        currency: 'USD',
      },
    },
    'fr-FR': {
      currency: {
        style: 'currency' as const,
        currency: 'EUR',
      },
    },
    'bg-BG': {
      currency: {
        style: 'currency' as const,
        currency: 'BGN',
      },
    },
  },
};
