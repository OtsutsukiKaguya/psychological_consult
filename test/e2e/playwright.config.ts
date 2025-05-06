import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: '../',                 // 相对于 config 文件的路径
  timeout: 30 * 1000,             // 单个测试最大超时 30s
  retries: 1,                     // CI 环境下失败重试一次
  workers: process.env.CI ? 2 : undefined,
  use: {
    headless: true,
    baseURL: 'https://localhost:5173',  
    viewport: { width: 1280, height: 720 },
    ignoreHTTPSErrors: true,
    video: 'retain-on-failure',
    actionTimeout: 10 * 1000,
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],
});