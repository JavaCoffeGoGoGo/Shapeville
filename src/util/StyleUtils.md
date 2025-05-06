## Java Swing 一般的构建组件的两种方式

### 1. 直接创建组件对象

#### 1）纯净无set
- 例子：`JLabel labelA = new JLabel("默认样式");`
- 内部逻辑：此时不会再取 UIManager 的字体
    - Java Swing 中的样式是通过 UIManager 控制的
    - 在Java Swing的某个类中写有类似  
      `UIManager.put("Label.font", new Font("Serif", Font.PLAIN, 16));`这样的默认值
    - 具体取法可大致阐述为（仍结合例子）：
        - 在new语句之后自动执行以下语句
            ```java
            labelA.setFont(UIManager.getFont("Label.font"));
            labelA.setForeground(UIManager.getColor("Label.foreground"));
            labelA.setBackground(UIManager.getColor("Label.background"));
            ```
    - 但还有一种方法可以不用每回都set，就是直接改全局设置
        - 一般流程如下
            ```java
            UIManager.put("Label.font", new Font("Courier", Font.ITALIC, 14));
            SwingUtilities.updateComponentTreeUI(frame);
            ```
        - 但是注意两点
            1. `UIManager.put()`仅对之后组件生效，需要利用`updateComponentTreeUI`进行全局更新  
               （可以把这一点放在所有程序的开头以规避风险）
            2. `UIManager.put()`的优先级仍低于`setFont()`等手动设置

#### 2）带set
- 例子：
    ```java
    JLabel labelB = new JLabel("我有自己的样式");
    labelB.setFont(new Font("Arial", Font.BOLD, 20)); // 手动设置字体
    ```
- 内部逻辑：此时不会再取 UIManager 的字体，但其他仍取UIManager的默认值，如背景等
    - Java Swing 中的样式是通过 UIManager 控制的
    - 具体取法大致变为（仍结合例子）：
        ```java
        // 在new语句和setFont语句后之后自动执行以下语句
        labelA.setForeground(UIManager.getColor("Label.foreground"));
        labelA.setBackground(UIManager.getColor("Label.background"));
        
        // 不再执行labelB.setFont(UIManager.getFont("Label.font"));
        ```

### 2. 继承组件类并构建自定义类
- 概括来讲，就是把
    ```java
    JPanel buttonGroup = new JPanel();
    buttonGroup.setLayout(new FlowLayout());
    buttonGroup.add(new JButton("一年级"));
    buttonGroup.add(new JButton("二年级"));
    ```
  中的：
    1. `JPanel buttonGroup = new JPanel();` 换成
        ```java
        public class buttonGroup extends JPanel {
            public ButtonGroupPanel() {
            }
        }
        ```
    2. 这三句中的`buttonGroup`换成`this`（可省略）并封装到内层`{}`里
        ```java
        this.setLayout(new FlowLayout());
        this.add(new JButton("一年级"));
        this.add(new JButton("二年级"));
        ```
- 也就是说，在带set与不带set的内层逻辑上与直接创建组件对象并无两样