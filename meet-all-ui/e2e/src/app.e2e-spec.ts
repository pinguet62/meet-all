import { AppPage } from './app.po';

describe('new App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should have page title', () => {
    page.navigateTo();
    expect(page.getPageTitle()).toContain('Meet-all');
  });
});
