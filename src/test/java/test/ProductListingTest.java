package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ProductDetailPage;
import pages.ProductPage;

import java.util.List;

public class ProductListingTest extends BaseTest {

    @BeforeMethod
    public void loginForProductTests() {
        requireBackend();
        loginUtils.loginWithDemoSession("Selenium Products", "selenium.products@example.com");
        openApp("/products");
        waitForUrlContains("/products");
    }

    @Test(description = "T080, T081: Automate product listing page and validate product visibility")
    public void productListingShowsVisibleProducts() {
        ProductPage productPage = new ProductPage(driver);

        Assert.assertTrue(productPage.isLoaded(), "Products page should load");
        Assert.assertTrue(productPage.productCount() > 0, "At least one product should be visible");
        Assert.assertFalse(productPage.firstProductName().isBlank(), "Visible product should show a name");
        Assert.assertFalse(productPage.firstProductPrice().isBlank(), "Visible product should show a price");
        Assert.assertFalse(productPage.firstProductStockText().isBlank(), "Visible product should show stock status");
    }

    @Test(description = "T082: Validate product details including name and price")
    public void productDetailsShowNameAndPrice() {
        ProductPage productPage = new ProductPage(driver);
        String listName = productPage.firstProductName();
        String listPrice = productPage.firstProductPrice();
        String listCategory = productPage.firstProductCategory();

        productPage.openFirstProduct();
        waitForUrlContains("/products/");

        ProductDetailPage detailPage = new ProductDetailPage(driver);
        Assert.assertTrue(detailPage.isLoaded(), "Product detail page should load");
        Assert.assertEquals(detailPage.productName(), listName, "Detail page should show the selected product name");
        Assert.assertEquals(detailPage.productPrice(), listPrice, "Detail page should show the selected product price");
        Assert.assertEquals(detailPage.productCategory(), listCategory, "Detail page should show the selected product category");
        Assert.assertFalse(detailPage.productDescription().isBlank(), "Detail page should show a product description");
        Assert.assertFalse(detailPage.stockText().isBlank(), "Detail page should show stock availability");

        if (detailPage.stockText().toLowerCase().contains("out of stock")) {
            Assert.assertFalse(detailPage.canAddToCart(), "Out-of-stock products should not be addable");
            Assert.assertFalse(detailPage.hasQuantitySelector(), "Out-of-stock products should not show quantity controls");
        } else {
            Assert.assertTrue(detailPage.canAddToCart(), "In-stock products should be addable");
            Assert.assertTrue(detailPage.hasQuantitySelector(), "In-stock products should show quantity controls");

            int startingQuantity = detailPage.quantity();
            detailPage.increaseQuantity();
            Assert.assertTrue(detailPage.quantity() >= startingQuantity, "Quantity control should handle dynamic updates");
        }
    }

    @Test(description = "Validate product search returns matching products")
    public void productSearchReturnsMatchingResults() {
        ProductPage productPage = new ProductPage(driver);
        Assert.assertTrue(productPage.isLoaded(), "Products page should load before searching");

        String firstProductName = productPage.firstProductName();
        String keyword = searchableKeyword(firstProductName);

        productPage.search(keyword);

        Assert.assertTrue(productPage.isLoadingMessageGone(), "Search loading state should settle");
        Assert.assertTrue(productPage.productCount() > 0, "Search should return at least one matching product");
        Assert.assertTrue(
                productPage.visibleProductNames().stream()
                        .allMatch(name -> name.toLowerCase().contains(keyword.toLowerCase())),
                "Every visible product name should contain the searched keyword"
        );
    }

    @Test(description = "Validate product category filtering")
    public void productCategoryFilterShowsOnlySelectedCategory() {
        ProductPage productPage = new ProductPage(driver);
        Assert.assertTrue(productPage.isLoaded(), "Products page should load before filtering");

        List<String> categories = productPage.availableCategories();
        if (categories.isEmpty()) {
            throw new SkipException("No categories available in the current product catalog");
        }

        String selectedCategory = categories.get(0);
        productPage.filterByCategory(selectedCategory);

        Assert.assertTrue(productPage.productCount() > 0, "Category filter should return products");
        Assert.assertTrue(
                productPage.visibleProductCategories().stream()
                        .allMatch(category -> category.equalsIgnoreCase(selectedCategory)),
                "Every visible product should belong to the selected category"
        );
    }

    @Test(description = "Handle dynamic empty product search results and reset state")
    public void productSearchHandlesEmptyResultsAndReset() {
        ProductPage productPage = new ProductPage(driver);
        Assert.assertTrue(productPage.isLoaded(), "Products page should load before empty search validation");
        int initialCount = productPage.productCount();

        productPage.search("no-product-" + System.currentTimeMillis());

        Assert.assertTrue(productPage.isLoadingMessageGone(), "Empty search loading state should settle");
        Assert.assertTrue(productPage.hasNoResultsMessage(), "No-result searches should show the empty state");

        productPage.resetFilters();
        Assert.assertEquals(productPage.productCount(), initialCount, "Reset should restore the original product list");
    }

    @Test(description = "T083: Verify product navigation from listing to detail and back")
    public void productNavigationMovesBetweenListingAndDetail() {
        ProductPage productPage = new ProductPage(driver);
        productPage.openFirstProduct();
        waitForUrlContains("/products/");

        ProductDetailPage detailPage = new ProductDetailPage(driver);
        Assert.assertTrue(detailPage.isLoaded(), "Product detail page should load after selecting a product");

        detailPage.backToProducts();
        waitForUrlContains("/products");
        Assert.assertTrue(new ProductPage(driver).isLoaded(), "Back action should return to product listing");
    }

    private String searchableKeyword(String productName) {
        String compactName = productName == null ? "" : productName.trim();
        if (compactName.length() < 3) {
            throw new SkipException("First product name is too short to build a meaningful search keyword");
        }
        return compactName.substring(0, Math.min(4, compactName.length()));
    }
}
