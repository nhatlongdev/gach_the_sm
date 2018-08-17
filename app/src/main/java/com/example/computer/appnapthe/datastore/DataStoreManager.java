package com.example.computer.appnapthe.datastore;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.computer.appnapthe.configs.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by phamv on 9/13/2016.
 */

public class DataStoreManager extends BaseDataStore {

    // ============== User ============================
    private static final String PREF_USER = "PREF_USER";
    private static final String PREF_TOKEN_USER = "PREF_TOKEN_USER";
    private static final String PREF_SETTINGS_NOTIFY = "PREF_SETTINGS_NOTIFY";
    private static final String PREF_SETTINGS_SOUND = "PREF_SETTINGS_SOUND";
    private static final String PREF_SETTINGS_VIBRATE = "PREF_SETTINGS_VIBRATE";

    private static final String PREF_SIZE_LIST_SHOP = "PREF_SIZE_LIST_SHOP";
    private static final String PREF_TABLE_ID = "PREF_TABLE_ID";
    private static final String PREF_LONG_TIME = "PREF_LONG_TIME";
    private static final String PREF_CONFIG_DISH = "PREF_CONFIG_DISH";
    private static final String PREF_JSON_TABLE = "PREF_JSON_TABLE";
    private static final String PREF_ID_SHOP = "PREF_ID_SHOP";
    private static final String PREF_POSITION = "PREF_POSITION";
    private static final String PREF_ID_SHOP_CHECK_CAKE = "PREF_ID_SHOP_CHECK_CAKE";
    private static final String PREF_EMAIL = "PREF_EMAIL";
    private static final String PREF_ERROR = "PREF_ERROR";
    private static final String PREF_CONFIG_URL = "PREF_CONFIG_URL";
    private static final String PREF_TO_FRAGMENT = "PREF_TO_FRAGMENT";
    private static final String PREF_COMMENT = "PREF_COMMENT";
    private static final String PREF_MENU = "PREF_MENU";
    private static final String PREF_ADDRESS_PRINTER = "PREF_ADDRESS_PRINTER";
    private static final String PREF_SHOP = "PREF_SHOP";
    private static final String PREF_BAR_LIST = "PREF_BAR_LIST";
    private static final String PREF_BILL_SELECT = "PREF_BILL_SELECT";
    private static final String PREF_ORDER_SELECT = "PREF_ORDER_SELECT";
    private static final String PREF_TABLE = "PREF_TABLE";
    private static final String PREF_TABLE_CHANGE = "PREF_TABLE_CHANGE";
    private static final String PREF_BILL = "PREF_BILL";
    private static final String PREF_PRINTER = "PREF_PRINTER";
    private static final String PREF_ORDER = "PREF_ORDER";
    private static final String PREF_ORDER_DETAIL = "PREF_ORDER_DETAIL";
    private static final String PREF_BAR = "PREF_BAR";
    private static final String PREF_COUNT_NEW_MESSAGE = "PREF_COUNT_NEW_MESSAGE";
    private static final String PREF_FLOOR_ID = "PREF_FLOOR_ID";
    private static final String PREF_BAR_ID = "PREF_BAR_ID";
    private static final String PREF_SECTION_ID = "PREF_SECTION_ID";
    private static final String PREF_CODE_LANGUAGE = "PREF_CODE_LANGUAGE";
    private static final String PREF_NAME_SECTION_SELECTED = "PREF_NAME_SECTION_SELECTED";
    private static final String PREF_STATUS_PRINTER = "PREF_STATUS_PRINTER";
    private static final String PREF_STATUS_BLUETOOTH = "PREF_STATUS_BLUETOOTH";

    private static final String PREF_JSON_MENU = "PREF_JSON_MENU";
    private static final String PREF_JSON_CATEGORY = "PREF_JSON_CATEGORY";
    private static final String PREF_JSON_DISH = "PREF_JSON_DISH";

    private static final String PREF_LAST_TIME_REQUEST = "PREF_LAST_TIME_REQUEST";
    private static final String PREF_USER_TOKEN = "PREF_USER_TOKEN";
    private static final String PREF_AREA_ID = "PREF_AREA_ID";
    private static final String STYLE_PRINT = "STYLE_PRINT";

    /*================NAP THE ===================*/
    private static final String PREF_CACHE_JSON_SIM = "PREF_CACHE_JSON_SIM";
    private static final String PREF_CACHE_MODE_NAP = "PREF_CACHE_MODE_NAP";
    private static final String PREF_CACHE_INFOR_NAP_THEO_SIM = "PREF_CACHE_INFOR_NAP_THEO_SIM";
    private static final String PREF_CACHE_CARD_LOADED_THEO_SIM= "PREF_CACHE_CARD_LOADED_THEO_SIM";
    private static final String PREF_CACHE_LIST_CARD_GET_MODE_AUTO= "PREF_CACHE_LIST_CARD_GET_MODE_AUTO";
    private static final String PREF_CACHE_ERROR_PUSH_VALUE_JSON= "PREF_CACHE_ERROR_PUSH_VALUE_JSON";

    /*save time*/
    public static void saveTime(long timeCurrent) {
        getInstance().sharedPreferences.putLongValue(PREF_LONG_TIME, timeCurrent);
    }
    public static long getTime() {
        return getInstance().sharedPreferences.getLongValue(PREF_LONG_TIME);
    }

    /*save config dish*/
    public static void saveConfigDish(int code) {
        getInstance().sharedPreferences.putIntValue(PREF_CONFIG_DISH, code);
    }
    public static int getConfigDish() {
        return getInstance().sharedPreferences.getIntValue(PREF_CONFIG_DISH);
    }

    /*save language*/
    public static void saveLanguage(String codeLanguage) {
        getInstance().sharedPreferences.putStringValue(PREF_CODE_LANGUAGE, codeLanguage);
    }
    public static String getLanguage() {
        return getInstance().sharedPreferences.getStringValue(PREF_CODE_LANGUAGE);
    }

    /*save AREA ID  */
    public static void saveAreaId(String areaId) {
        getInstance().sharedPreferences.putStringValue(PREF_AREA_ID, areaId);
    }
    public static String getAreaId() {
        return getInstance().sharedPreferences.getStringValue(PREF_AREA_ID);
    }


    /*save statu connect printer*/
    public static void saveStatusConnectPrinter(boolean t) {
        getInstance().sharedPreferences.putBooleanValue(PREF_STATUS_PRINTER, t);
    }
    public static boolean getStatusConnectPrinter() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_STATUS_PRINTER);
    }

     /*save statu connect BLUETOOTH*/
    public static void saveStatusConnectBluetooth(boolean t) {
        getInstance().sharedPreferences.putBooleanValue(PREF_STATUS_BLUETOOTH, t);
    }
    public static boolean getStatusConnectBluetooth() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_STATUS_BLUETOOTH);
    }


    /*save idBill select*/
    public static void saveIdBill(int idBill) {
        getInstance().sharedPreferences.putIntValue(PREF_ID_SHOP, idBill);
    }
    public static int getIdBill() {
        return getInstance().sharedPreferences.getIntValue(PREF_ID_SHOP);
    }

    /*save id floor*/
    public static void saveFloorId(String floor_id) {
        getInstance().sharedPreferences.putStringValue(PREF_FLOOR_ID, floor_id);
    }
    public static String getFloorId() {
        return getInstance().sharedPreferences.getStringValue(PREF_FLOOR_ID);
    }

    /*save COUNT NEW MESSAGE*/
    public static void saveCountNewMessage(int countNewMessage) {
        getInstance().sharedPreferences.putIntValue(PREF_COUNT_NEW_MESSAGE, countNewMessage);
    }
    public static int getCountNewMessage() {
        return getInstance().sharedPreferences.getIntValue(PREF_COUNT_NEW_MESSAGE);
    }

    public static void removeCountNew() {
        getInstance().sharedPreferences.putIntValue(PREF_COUNT_NEW_MESSAGE, 0);
    }


    /**
     * save and get user's token
     *
     */
    public static void saveToken(String token) {
        getInstance().sharedPreferences.putStringValue(PREF_TOKEN_USER, token);
    }
    public static String getToken() {
        return getInstance().sharedPreferences.getStringValue(PREF_TOKEN_USER);
    }

    /**
     * save and get caching time
     *
     */
    public static void saveEmail(String email) {
        getInstance().sharedPreferences.putStringValue(PREF_EMAIL, email);
    }
    public static String getEmail() {
        return getInstance().sharedPreferences.getStringValue(PREF_EMAIL);
    }

    /*save error*/
    public static void saveError(String error1) {
        getInstance().sharedPreferences.putStringValue(PREF_ERROR, error1);
    }
    public static String getError() {
        return getInstance().sharedPreferences.getStringValue(PREF_ERROR);
    }

    public static void saveConfigAlla(String configUrl) {
        getInstance().sharedPreferences.putStringValue(PREF_CONFIG_URL, configUrl);
    }
    public static String getConfigAlla() {
        return getInstance().sharedPreferences.getStringValue(PREF_CONFIG_URL);
    }

    public static void saveToFragment(int toFragment) {
        getInstance().sharedPreferences.putIntValue(PREF_TO_FRAGMENT, toFragment);
    }
    public static int getToFragment() {
        return getInstance().sharedPreferences.getIntValue(PREF_TO_FRAGMENT);
    }

    /*save commnet*/

    public static void saveComment(String comment) {
        getInstance().sharedPreferences.putStringValue(PREF_COMMENT, comment);
    }
    public static String getComment() {
        return getInstance().sharedPreferences.getStringValue(PREF_COMMENT);
    }

    /*save address printer*/
    public static void saveAddressPrinter(String addressPrinter) {
        getInstance().sharedPreferences.putStringValue(PREF_ADDRESS_PRINTER, addressPrinter);
    }
    public static String getAddressPrinter() {
        return getInstance().sharedPreferences.getStringValue(PREF_ADDRESS_PRINTER);
    }

    //================== settings notify ======================
    public static void saveSettingNotify(boolean state){
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_NOTIFY, state);
    }

    public static boolean getSettingNotifyState(){
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_NOTIFY);
    }

    /*saveSetting sound*/
    public static void saveSettingSound(boolean state){
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_SOUND, state);
    }

    public static boolean getSettingSound(){
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_SOUND);
    }

    /*saveSetting vibrate*/
    public static void saveSettingVibrate(boolean state){
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_VIBRATE, state);
    }

    public static boolean getSettingVibrate(){
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_VIBRATE);
    }



    /*============================================NAP THE =======================================*/
    /*luu che do nap hien tai*/
    public static void saveCheDoNap(String mode){
        getInstance().sharedPreferences.putStringValue(PREF_CACHE_MODE_NAP, mode);
    }

    public static String getCheDoNap(){
        return getInstance().sharedPreferences.getStringValue(PREF_CACHE_MODE_NAP);
    }


    /*luu cache thong tin sim theo may*/
    public static void saveCacheJsonSim(String jsonSim){
        getInstance().sharedPreferences.putStringValue(PREF_CACHE_JSON_SIM, jsonSim);
    }

    public static String getCacheJsonSim(){
        return getInstance().sharedPreferences.getStringValue(PREF_CACHE_JSON_SIM);
    }

    /*luu cache thong tin nap theo sim*/
    public static void saveInforNapTheTheoSim(String jsonNapTheSim){
        getInstance().sharedPreferences.putStringValue(PREF_CACHE_INFOR_NAP_THEO_SIM, jsonNapTheSim);
    }

    public static String getInforNapTheTheoSim(){
        return getInstance().sharedPreferences.getStringValue(PREF_CACHE_INFOR_NAP_THEO_SIM);
    }

    /*luu cache thong tin cac the da nap theo sim*/
    public static void saveCardNapTheoSim(String jsonCardNapTheSim){
        getInstance().sharedPreferences.putStringValue(PREF_CACHE_CARD_LOADED_THEO_SIM, jsonCardNapTheSim);
    }

    public static String getCardNapTheoSim(){
        return getInstance().sharedPreferences.getStringValue(PREF_CACHE_CARD_LOADED_THEO_SIM);
    }

    /*luu truong hop put value vao json lôi*/
    public static void saveErrorPutValeuToJson(String error){
        getInstance().sharedPreferences.putStringValue(PREF_CACHE_ERROR_PUSH_VALUE_JSON, error);
    }

    public static String getErrorPutValeuToJson(){
        return getInstance().sharedPreferences.getStringValue(PREF_CACHE_ERROR_PUSH_VALUE_JSON);
    }

    /*lưu ds thẻ đã lấy về khi ở chế độ nạp tự động*/
    public static void saveListCardGetModeAuto(String json){
        getInstance().sharedPreferences.putStringValue(PREF_CACHE_LIST_CARD_GET_MODE_AUTO, json);
    }

    public static String getListCardGetModeAuto(){
        return getInstance().sharedPreferences.getStringValue(PREF_CACHE_LIST_CARD_GET_MODE_AUTO);
    }

    /*save list suggest card*/
    public static void saveListSuggestCard(Context context, List<String> callLog) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constant.CALL_HISTORY_RC, context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(callLog);
        prefsEditor.putString("listSuggestCard", json);
        prefsEditor.commit();
    }

    public static List<String> getListSuggestCard(Context context) {
        List<String> callLog = new ArrayList<String>();
        SharedPreferences mPrefs = context.getSharedPreferences(Constant.CALL_HISTORY_RC, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("listSuggestCard", "");
        if (json.isEmpty()) {
            callLog = new ArrayList<String>();
        } else {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            callLog = gson.fromJson(json, type);
        }
        return callLog;
    }

    /*save list suggest code customer*/
    public static void saveListSuggestCodeCustomer(Context context, List<String> callLog) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constant.CALL_HISTORY_RC, context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(callLog);
        prefsEditor.putString("listSuggestCodeCustomer", json);
        prefsEditor.commit();
    }

    public static List<String> getListSuggestCodeCustomer(Context context) {
        List<String> callLog = new ArrayList<String>();
        SharedPreferences mPrefs = context.getSharedPreferences(Constant.CALL_HISTORY_RC, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("listSuggestCodeCustomer", "");
        if (json.isEmpty()) {
            callLog = new ArrayList<String>();
        } else {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            callLog = gson.fromJson(json, type);
        }
        return callLog;
    }
}
