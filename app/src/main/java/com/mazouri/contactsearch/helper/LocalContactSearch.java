package com.mazouri.contactsearch.helper;

import android.text.TextUtils;
import android.util.Log;

import com.mazouri.contactsearch.model.ContactBean;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangdongdong on 17-4-12.
 */

public class LocalContactSearch {
    private static final String TAG = LocalContactSearch.class.getSimpleName();

    /**
     * 按拼音搜索
     *
     * @param str
     */
    public static ArrayList<ContactBean> searchContact(CharSequence str,
                                                     ArrayList<ContactBean> allContacts) {
        ArrayList<ContactBean> contactBeanList = new ArrayList<ContactBean>();
        // 如果搜索条件以0 1 +开头则按号码搜索
        if (str.toString().startsWith("0") || str.toString().startsWith("1")
                || str.toString().startsWith("+")) {
            for (ContactBean contactBean : allContacts) {
                if (getContactBeanName(contactBean) != null
                        && contactBean.getPhoneNum() != null) {
                    if ((contactBean.getPhoneNum()).contains(str)
                            || getContactBeanName(contactBean).contains(str)) {
                        Log.d(TAG, "searchContact" + contactBean.getDesplayName() + ", " + contactBean.getPhoneNum());
                        Log.d(TAG, "searchContact ContactBeanName: " + getContactBeanName(contactBean));
                        contactBeanList.add(contactBean);
                    }
                }
            }
            return contactBeanList;
        }
        CharacterParser finder = CharacterParser.getInstance();

        String result = "";
        for (ContactBean contactBean : allContacts) {
            // 先将输入的字符串转换为拼音
            finder.setResource(str.toString());
            result = finder.getSpelling();
            if (contains(contactBean, result)) {
                contactBeanList.add(contactBean);
            } else if ((contactBean.getPhoneNum()).contains(str)) {
                contactBeanList.add(contactBean);
            }
        }
        return contactBeanList;
    }

    /**
     * 根据拼音搜索
     *
     */
    private static boolean contains(ContactBean contactBean, String search) {
        if (TextUtils.isEmpty(contactBean.getPhoneNum())
                && TextUtils.isEmpty(contactBean.getDesplayName())) {
            return false;
        }

        boolean flag = false;

        // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
        if (search.length() < 6) {
            String firstLetters = FirstLetterUtil
                    .getFirstLetter(getContactBeanName(contactBean));
            // 不区分大小写
            Pattern firstLetterMatcher = Pattern.compile(search,
                    Pattern.CASE_INSENSITIVE);
            flag = firstLetterMatcher.matcher(firstLetters).find();
        }

        if (!flag) { // 如果简拼已经找到了，就不使用全拼了
            // 全拼匹配
            CharacterParser finder = CharacterParser.getInstance();
            finder.setResource(getContactBeanName(contactBean));
            // 不区分大小写
            Pattern pattern2 = Pattern
                    .compile(search, Pattern.CASE_INSENSITIVE);
            Matcher matcher2 = pattern2.matcher(finder.getSpelling());
            flag = matcher2.find();
        }

        return flag;
    }

    private static String getContactBeanName(ContactBean contactBean) {
        String strName = null;
        if (!TextUtils.isEmpty(contactBean.getDesplayName())) {
            strName = contactBean.getDesplayName();
        } else if (!TextUtils.isEmpty(contactBean.getPhoneNum())) {
            strName = contactBean.getPhoneNum();
        } else {
            strName = "";
        }

        return strName;
    }
}
