import tseslint from 'typescript-eslint';
import prettierPlugin from 'eslint-plugin-prettier';
import prettierConfig from 'eslint-config-prettier';

export default tseslint.config(
    {
        ignores: ['dist/**', 'node_modules/**']
    },
    {
        files: ['**/*.ts'],
        extends: [...tseslint.configs.recommended],
        plugins: { prettier: prettierPlugin },
        rules: {
            ...prettierConfig.rules,
            'prettier/prettier': 'error',
            '@typescript-eslint/no-explicit-any': 'error',
            '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }]
        }
    },
    {
        // Sakai NG template files — any é necessário para chart.js e eventos DOM genéricos
        files: ['src/app/layout/**/*.ts', 'src/app/features/dashboard/**/*.ts', 'src/app/core/services/product.service.ts'],
        rules: {
            '@typescript-eslint/no-explicit-any': 'warn'
        }
    }
);
