public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void plusX(){
        this.x++;
    }

    public void plusY(){
        this.y++;
    }

    public void minusX(){
        this.x--;
    }

    public void minusY(){
        this.y--;
    }

}
