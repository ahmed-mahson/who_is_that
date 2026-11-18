# Who is that? (من هو ذلك؟)

تطبيق Android (Kotlin) مستوحى من ثيم `who_is_that_android` ويعمل مثل تطبيق "ارقام اليمن بلس":
- البحث عن هوية الأرقام اليمنية بالاسم أو الرقم.
- دعم جميع شبكات الاتصال (يمن موبايل، YOU، سبأفون، واي، يمن نت).
- واجهات RTL مع ثيم `Theme.WhoIsThat`.

## هيكل المشروع
- `app/src/main/res/` : موارد الثيم (layouts, colors, strings, drawables).
- `app/src/main/java/com/whoisthat/app/` : كود Kotlin (Activities, Adapter, Repository).

## البناء
افتح المشروع في Android Studio (Gradle) وقم بالبناء. الحد الأدنى SDK 21.