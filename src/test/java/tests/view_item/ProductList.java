package tests.view_item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProductList {

    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public List<Product> getIncludedProducts() {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.isIncluded()) {
                result.add(product);
            }
        }
        return result;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public static class Product {
        /** The product item id used in the url. */
        private String itemId;
        /** A note to the developer that helps identify the product. */
        private String note;
        /** The product title as it appears on the top of the VI page. */
        private String productTitle;
        /** The condition of the book (e.g. "Brand New") */
        private String condition;
        /** The name of the seller. */
        private String sellerName;
        /** The price of the item (in the current currency). */
        private double price;
        /** Should this product be included in the test run. */
        private boolean included;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
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

        public boolean isIncluded() {
            return included;
        }

        public void setIncluded(boolean included) {
            this.included = included;
        }

        @Override
        public String toString() {
            return this.note + " : " + itemId;
        }
    }

}
