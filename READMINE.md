ABCPenRecorder SDK v1.0.6

笔声白板录制sdk v1.0.6

接口版本1.0（后续持续更新）

主要功能：
白板绘制，选择线条颜色，线宽，截取白板图片，录制MP4视频等。

项目使用：
环境：Android Studio 2.3.3 以上，使用gradle3.3 以上。

	核心maven库引用
    	compile 'com.abcpen:open_wb_record:2.0.0'
    	

使用方式一：白板核心库接口API：


    /**
     * 设置loading 状态
     *
     * @param loading
     */
    boolean setLoading(boolean loading);

    /**
     * 加载页面完成
     *
     * @param pageindex
     */
    boolean endPageData(int pageindex);

    /**
     * 添加水印
     *
     * @param top
     * @param left
     * @param bottom
     * @param right
     */
    boolean setWaterMargin(float top, float left, float bottom, float right);

    /**
     * 重置pdf
     */
    boolean resetPDF();

    /**
     * 设置为pdf 模式
     *
     * @return
     */
    boolean setPdfModeIfSdcardExists();

    /**
     * 清理背景图片
     */
    boolean clearBgPics();

    /**
     * 读取白板文件
     *
     * @param path
     * @param pageNo
     * @return
     */
    List<String> readFile(String path, int pageNo);

    /**
     * 解析白板数据文件
     *
     * @param commands
     * @param i
     * @param pageNo
     */
    boolean parsePageData(List<String> commands, int i, int pageNo);

    /**
     * 按下
     *
     * @param mX
     * @param mY
     * @param calPress
     */
    boolean doMouseDown(float mX, float mY, float calPress);

    /**
     * 抬起
     *
     * @param mX
     * @param mY
     * @param calPress
     */
    boolean doMouseUp(float mX, float mY, float calPress);

    /**
     * 拖动
     *
     * @param mX
     * @param mY
     * @param calPress
     */
    boolean doMouseDragged(float mX, float mY, float calPress);

    /**
     * 悬浮
     *
     * @param mX
     * @param mY
     */
    boolean doHangDraw(float mX, float mY);

    /**
     * 结束录制
     *
     * @param source
     * @param dest
     * @param isPreview
     * @return
     */
    String finishRecord(String source, String dest, boolean isPreview);

    /**
     * 获取当前页
     *
     * @return
     */
    int getCurrentPage();

    /**
     * 是否正在录制
     *
     * @return
     */
    boolean isRecording();

    /**
     * 结束录制
     */
    boolean stopRecord();

    /**
     * 开始录制
     */
    boolean startRecord();

    /**
     * 设置白板参数
     *
     * @param b
     */
    void setArguments(Bundle b);

    /**
     * 是否垂直
     *
     * @param b
     */
    boolean setIsVertical(boolean b);

    /**
     * 设置录制的监听回调
     *
     * @param onPanelListener
     */
    boolean setOnPanelListener(OnPanelListener onPanelListener);

    /**
     * 设置白板监听回调
     *
     * @param wbInterface
     */
    boolean attachWhiteBoard(WBInterface wbInterface);

    /**
     * 设置手写笔的区域
     *
     * @param topLeftX
     * @param topLeftY
     * @param bottomRightX
     * @param bottomRightY
     */
    boolean setPenRegion(float topLeftX, float topLeftY, float bottomRightX, float bottomRightY);

    /**
     * 获取cache路径
     *
     * @return
     */
    String getCachePath();

    /**
     * 设置绘画模式
     *
     * @param freehandMode
     */
    boolean setDrawMode(int freehandMode);

    /**
     * 设置绘画工具
     *
     * @param mColorIndex
     * @param mWidthIndex
     * @param mPenType
     */
    boolean setToolType(int mColorIndex, int mWidthIndex, int mPenType);

    /**
     * 清理当前页面内容
     */
    void clearCurrentScreen();

    /**
     * 直接切换页面到pageindex
     *
     * @param pageindex
     */
    boolean resetPDFScreen(int pageindex);

    /**
     * 设置pdf 路径
     *
     * @param pdfPath
     */
    boolean setPdfPath(String pdfPath);

    /**
     * 设置缓存路径
     *
     * @param cacheDirFromPath
     */
    boolean setCachePath(String cacheDirFromPath);

    /**
     * 添加pdf
     *
     * @param uri
     */
    boolean sendAddPDF(String uri);

    /**
     * 系统截图
     *
     * @param screenShotFileName
     * @param wbInterface
     */
    boolean getScreenShot(String screenShotFileName, WBInterface wbInterface);

    /**
     * 超出范围重置
     *
     * @param x
     * @param y
     */
    boolean resetTouchUp(int x, int y);

    /**
     * 是否是pdf 模式
     *
     * @return
     */
    boolean isPDFMode();

    /**
     * 切换页面
     *
     * @param b
     */
    boolean changeScreen(boolean b);

    /**
     * 重做
     *
     * @return
     */
    boolean redo();

    /**
     * 撤销
     *
     * @return
     */
    boolean undo();

    /**
     * 添加图片
     *
     * @param wbImageModel
     */
    boolean addPhotoImage(WBImageModel wbImageModel);

    /**
     * 重置放缩
     */
    boolean resetScale();

    /**
     * 获得白板大小
     *
     * @return
     */
    Point getSize();

    /**
     * 设置总页数
     *
     * @param totalPDFPage
     */
    void setTotalPage(int totalPDFPage);

    /**
     * pdf图
     *
     * @param wbImageModel
     */
    void addPDFImage(WBImageModel wbImageModel);

    /**
     * 获取本页数据
     *
     * @return
     */
    Page20 getCurrentUserPage();

    /**
     * 手写识别
     *
     * @param isRecognizing 开始或者停止识别
     */
    void handWrtingRecognize(final boolean isRecognizing);

    /**
     * 绘制识别结果
     *
     * @param label 识别结果
     */
    void drawRecoResult(final String label, final int x, final int y);

    /**
     * 切换页码
     */
    void doChangeScreen(final int index);


    /**
     * 是否编辑
     *
     * @return
     */
    boolean getIsEdit();

    /**
     * 换页
     * @param index
     * @return
     */
    boolean changeScreen(final int index);


    boolean clearPDF();
    


使用方式2:

继承BaseWeikeActivity。参见Demo。

联系方式：shaoxiaoze@abcpen.com

    

