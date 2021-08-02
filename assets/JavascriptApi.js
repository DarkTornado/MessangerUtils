function print(msg) {
    Api.showToast(msg, 1);
}

function require(moduleName) {
    var module = {};
    module.exports = {};
    var exports = module.exports;
    try {
        var sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        var path = sdcard + "/MsgUtils/modules/" + moduleName;
        var file = new java.io.File(path);
        if (!file.exists()) {
            path = sdcard + "/MsgUtils/modules/" + moduleName + ".js";
            file = new java.io.File(path);
        }
        if (!file.exists()) {
            print(moduleName + "(이)라는 모듈을 찾을 수 없습니다.");
            return null;
        }
        var src = com.darktornado.nusty.Nusty.readFile(path);
        if (src == null) {
            print(moduleName + "(이)라는 모듈을 읽을 수 없습니다.");
            return null;
        }
        eval(src + "");
        return module.exports;
    } catch (e) {
        print(moduleName + "(이)라는 모듈을 불러오는 중 오류가 발생하였습니다.\n" + e + "\nAt: " + e.lineNumber);
    }
}

