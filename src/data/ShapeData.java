package data;

//提供统一的数据模型与随机题目生成、公式说明与面积计算接口。
// src/data/ShapeData.java

public class ShapeData {
    private String type;          // e.g., "square", "rectangle", "circle", "triangle", "trapezoid", "parallelogram"
    private double[] params;      // type-specific parameters: [side], [width, height], [radius], [base, height], etc.
    private String formula;       // area formula string for display, e.g. "Area = base × height / 2"

    public ShapeData(String type, double[] params, String formula) {
        this.type = type;
        this.params = params;
        this.formula = formula;
    }

    /** 随机生成一个基础二维图形及其参数 */
    public static ShapeData random2DShape() {
        String[] types = {"square", "rectangle", "triangle", "trapezoid", "parallelogram"};
        String t = types[new Random().nextInt(types.length)];
        double[] p;
        String f;
        switch (t) {
            case "square":
                double side = 1 + new Random().nextInt(20);
                p = new double[]{side};
                f = "Area = side × side";
                break;
            case "rectangle":
                double w = 1 + new Random().nextInt(20);
                double h = 1 + new Random().nextInt(20);
                p = new double[]{w, h};
                f = "Area = width × height";
                break;
            case "triangle":
                double base = 1 + new Random().nextInt(20);
                double height = 1 + new Random().nextInt(20);
                p = new double[]{base, height};
                f = "Area = base × height / 2";
                break;
            case "trapezoid":
                double a = 1 + new Random().nextInt(20);
                double b = 1 + new Random().nextInt(20);
                double ht = 1 + new Random().nextInt(20);
                p = new double[]{a, b, ht};
                f = "Area = (a + b) ÷ 2 × height";
                break;
            case "parallelogram":
                double base2 = 1 + new Random().nextInt(20);
                double height2 = 1 + new Random().nextInt(20);
                p = new double[]{base2, height2};
                f = "Area = base × height";
                break;
            default:
                p = new double[]{};
                f = "";
        }
        return new ShapeData(t, p, f);
    }

    /** 计算该图形的面积，保留一位小数 */
    public double calculateArea() {
        double area;
        switch (type) {
            case "square":
                area = params[0] * params[0];
                break;
            case "rectangle":
                area = params[0] * params[1];
                break;
            case "triangle":
                area = params[0] * params[1] / 2;
                break;
            case "trapezoid":
                area = (params[0] + params[1]) / 2 * params[2];
                break;
            case "parallelogram":
                area = params[0] * params[1];
                break;
            default:
                area = 0;
        }
        return Math.round(area * 10) / 10.0;
    }

    /** 获取展示用的公式及参数带入说明 */
    public String getFormulaWithValues() {
        String filled = formula;
        // 可扩展：替换公式字符串中的占位符为 params 值
        return filled + " (参数：" + Arrays.toString(params) + ")";
    }

    public String getType() { return type; }
    public double[] getParams() { return params; }
    public String getFormula() { return formula; }
}
