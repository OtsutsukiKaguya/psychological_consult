import { test, expect } from '@playwright/test';

test.describe('访客登录流程', () => {
  test('should login successfully and navigate to dashboard', async ({ page }) => {
    // 打开前端首页
    await page.goto('https://localhost:5173');
    // 填写手机号
    await page.getByPlaceholder('手机号').fill('13800000000');
    // 填写验证码
    await page.getByPlaceholder('验证码').fill('000000');
    // 点击登录
    await page.getByRole('button', { name: '登录' }).click();
    // 登录成功后应展示仪表盘关键文字
    await expect(page.getByText('今日可预约咨询师')).toBeVisible();
    // URL 跳转检查（可根据实际路由调整）
    await expect(page).toHaveURL(/dashboard/);
  });
});
