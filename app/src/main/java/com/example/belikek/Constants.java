package com.example.belikek;

public final class Constants {
    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
    // ================================
    // TOYYIBPAY ATTRIBUTES
    // ================================
    public static final String TOYYIBPAY_USER_SECRET_CODE = "sh02eahr-60s5-z9hj-trxt-3hw4ntisy07h";
    public static final String TOYYIBPAY_CATEGORY_CODE = "uxi1zyfd";
    public static final String TOYYIBPAY_URL = "https://toyyibpay.com/";
    public static final String TOYYIBPAY_BILL_PAYMENT_STATUS = "billpaymentStatus";

    // ================================
    // COLLECTION NAMES
    // ================================
    public static final String CAKE_OPTIONS_COLLECTION = "cake_options";
    public static final String CATEGORIES_COLLECTION = "categories";
    public static final String ORDERS_COLLECTION = "orders";
    public static final String PRODUCTS_COLLECTION = "products";

    // ================================
    // DOCUMENT NAMES
    // ================================
    public static final String CAKE_BASES_DOC = "cake_bases";
    public static final String DECORATIONS_DOC = "decorations";
    public static final String FILLINGS_DOC = "fillings";

    // ================================
    // FIELD NAMES
    // ================================
    public static final String PAYMENT_STATUS_FIELD = "payment_status";
    public static final String USER_ID_FIELD = "user_id";
    public static final String ITEMS_FIELD = "items";
    public static final String BASE_PRICE_FIELD = "base_price";
    public static final String CUSTOMIZATIONS_FIELD = "customizations";
    public static final String CATEGORY_ID_FIELD = "category_id";
    public static final String CAKE_BASE_FIELD = "cake_base";
    public static final String FILLINGS_FIELD = "fillings";
    public static final String DECORATIONS_FIELD = "decorations";
    public static final String OPTION_ID_FIELD = "option_id";
    public static final String OPTION_NAME_FIELD = "option_name";
    public static final String PRODUCT_NAME_FIELD = "product_name";
    public static final String PRODUCT_ID_FIELD = "product_id";
    public static final String QUANTITY_FIELD = "quantity";
    public static final String IMAGE_URL_FIELD = "image_url";
    public static final String ID_FIELD = "id";
    public static final String BILL_CODE_FIELD = "bill_code";
    public static final String UPDATED_AT_FIELD = "updated_at";
    public static final String PAYMENT_URL_FIELD = "payment_url";
    public static final String TOTAL_AMOUNT_FIELD = "total_amount";
    public static final String DISPLAY_ORDER_FIELD = "display_order";
    public static final String IS_ACTIVE_FIELD = "is_active";
    public static final String NAME_FIELD = "name";
    public static final String PRICE_FIELD = "price";
    public static final String HAS_CAKE_OPTIONS_FIELD = "hasCakeOptions";


}
