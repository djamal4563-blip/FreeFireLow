# Free Fire Low-End Booster — Android

هذه نسخة Android حقيقية تعتمد على تدخلات Game Mode في Android عندما يدعمها الجهاز، مع Shizuku للوصول إلى أوامر shell بصلاحية ADB/Root.

ما يفعله زر "تطبيق":
1. يحدد Free Fire (الحزمة العالمية com.dts.freefireth أو MAX com.dts.freefiremax).
2. يطلب من النظام إعداد Game Mode Performance.
3. يضع downscaleFactor = 0.8 أو 0.7 أو 0.6 عبر game_overlay.
4. يجب إعادة تشغيل Free Fire.

هذا ليس aimbot ولا يعدل ملفات اللعبة. الهدف خفض دقة الـbackbuffer لتقليل حمل GPU عندما يدعم Android/OEM هذا التدخل.

مهم:
- التطبيق لا يستطيع ضمان الدعم على كل هاتف.
- Shizuku مطلوب لأن التطبيق العادي لا يملك صلاحية shell لتغيير Game Mode لتطبيق آخر.
- Shizuku على جهاز غير مروّت يمكن تشغيله عبر Wireless Debugging/ADB حسب إصدار Android.
- على بعض الأجهزة قد تكون تدخلات Game Mode غير متاحة أو مقيدة من الشركة المصنعة.
- قيمة 0.7 تعني دقة عرض تقريبية 70% من الأصل، وليس "جودة اللعبة 70%" حرفيًا.

البناء:
افتح المشروع في Android Studio ثم Build > Build APK(s).

المصادر الرسمية:
Android Game Mode: https://developer.android.com/games/optimize/adpf/gamemode/gamemode-interventions
Shizuku API: https://github.com/RikkaApps/Shizuku-API
