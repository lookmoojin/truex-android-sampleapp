-keep class
    com.truedigital.component.model.**,
    com.truedigital.features.article.widget.share.ShareWidgetModel.**
    { *; }

# Workaround from LOG ==============================================================================
-keep class **.R$* {
    public static <fields>;
}