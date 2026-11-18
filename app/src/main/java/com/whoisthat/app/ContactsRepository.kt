package com.whoisthat.app

object ContactsRepository {

    private val all: List<ContactEntry> = listOf(
        ContactEntry("أحمد محمد", "771234567", "يمن موبايل"),
        ContactEntry("محمد علي", "772345678", "يمن موبايل"),
        ContactEntry("فاطمة حسن", "733456789", "YOU"),
        ContactEntry("خالد سعيد", "714567890", "سبأفون"),
        ContactEntry("سارة عبدالله", "705678901", "واي"),
        ContactEntry("عبدالرحمن ناصر", "776789012", "يمن موبايل"),
        ContactEntry("مريم أحمد", "737890123", "YOU"),
        ContactEntry("يوسف إبراهيم", "718901234", "سبأفون"),
        ContactEntry("نورة سالم", "709012345", "واي"),
        ContactEntry("عمر قاسم", "770123456", "يمن موبايل"),
        ContactEntry("هدى محمد", "731234567", "YOU"),
        ContactEntry("إبراهيم قاسم", "712345678", "سبأفون"),
        ContactEntry("رانيا طارق", "703456789", "واي"),
        ContactEntry("ماجد فهد", "774567890", "يمن موبايل"),
        ContactEntry("لمى خالد", "735678901", "YOU"),
        ContactEntry("بدر سلطان", "716789012", "سبأفون"),
        ContactEntry("سعاد نبيل", "707890123", "واي"),
        ContactEntry("توفيق حمود", "778901234", "يمن موبايل"),
        ContactEntry("هناء راشد", "739012345", "YOU"),
        ContactEntry("وليد منصور", "710123456", "سبأفون"),
        ContactEntry("نادية سعيد", "771112233", "يمن نت")
    )

    fun networks(): List<String> = listOf(
        "جميع الشبكات", "يمن موبايل", "YOU", "سبأفون", "واي", "يمن نت"
    )

    private fun isAll(network: String): Boolean = network == "جميع الشبكات"

    fun searchByNumber(query: String, network: String): List<ContactEntry> {
        val q = query.trim().replace(" ", "")
        if (q.isEmpty()) return emptyList()
        return all.filter { entry ->
            entry.number.contains(q) && (isAll(network) || entry.network == network)
        }
    }

    fun searchByName(query: String, network: String): List<ContactEntry> {
        val q = query.trim()
        if (q.isEmpty()) return all.filter { isAll(network) || it.network == network }
        return all.filter { entry ->
            entry.name.contains(q, ignoreCase = true) && (isAll(network) || entry.network == network)
        }
    }
}