package g3.viewchoosephoto;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

public class ResizeView {
    private static int WIDTH_STAND = 1080;

    public static DisplayMetrics getDisplayInfo() {
        return Resources.getSystem().getDisplayMetrics();
    }

    public static void resizeView(View view, int width, int height) {
        int pW = getDisplayInfo().widthPixels * width / WIDTH_STAND;
        int pH = pW * height / width;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = pW;
        params.height = pH;
    }
}
