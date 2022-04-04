package tests.view_item;

import java.util.List;

public class ProductList {

    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public static class Product {
        /** The product item id used in the url. */
        private String itemId;
        /** The product title as it appears on the top of the VI page. */
        private String productTitle;
        /** The condition of the book (e.g. "Brand New") */
        private String condition;
        /** The name of the seller. */
        private String sellerName;
        /** The price of the item (in the current currency). */
        private double price;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getProductTitle() {
            return productTitle;
        }

        public void setProductTitle(String productTitle) {
            this.productTitle = productTitle;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getSellerName() {
            return sellerName;
        }

        public void setSellerName(String sellerName) {
            this.sellerName = sellerName;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return this.itemId + ", " + this.productTitle + ", " + this.condition +
                    ", "  + this.sellerName + ", " + this.price;
        }
    }

}
