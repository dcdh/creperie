import { defineConfig } from 'orval';

// generate only for front tagged endpoints
export default defineConfig({
  front: {
    output: {
      mode: 'split',
      target: 'src/api/endpoints.ts',
      schemas: 'src/api/model',
      prettier: true,
      override: {
        mutator: {
          path: "src/api/mutator/custom-instance.ts",
          name: "customInstance",
        },
      }
    },
    input: {
      target: './../../../target/openapi.yaml',
    },
    hooks: {
      afterAllFilesWrite: 'prettier --write',
    },
  },
});
