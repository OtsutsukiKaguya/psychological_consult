import { test, expect } from '@playwright/test';

test.describe('访客预约咨询流程', () => {

  test.beforeEach(async ({ page }) => {
    // 复用登录逻辑
    await page.goto('https://localhost:5173');
    await page.getByPlaceholder('手机号').fill('13800000000');
    await page.getByPlaceholder('验证码').fill('000000');
    await page.getByRole('button', { name: '登录' }).click();
    await expect(page.getByText('今日可预约咨询师')).toBeVisible();
  });

  test('should create a new reservation', async ({ page }) => {
    // 点击“预约咨询师”进入预约页面
    await page.getByText('预约咨询师').click();

    // 选择咨询师（根据实际按钮或列表项调整）
    await page.getByText('张心理').click();

    // 选择日期
    await page.getByPlaceholder('选择日期').fill('2025-05-10');
    // 选择开始时间
    await page.getByPlaceholder('开始时间').fill('14:00');
    // 选择结束时间
    await page.getByPlaceholder('结束时间').fill('15:00');

    // 提交预约
    await page.getByRole('button', { name: '提交预约' }).click();

    // 验证预约成功提示
    await expect(page.getByText('预约成功')).toBeVisible();
  });
});