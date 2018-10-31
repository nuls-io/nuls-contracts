# pixel-master

This is a simple smart contract similar to the eos pixel master.
There are four methods in the contract, namely

    void buy(Short x, Short y, Short red, Short blue, Short yellow);
    
    PixelEntity queryPixel(Short x, Short y);
    
    String info();
    
    boolean tryStop();

The following describes the function usage
1. When the contract is deployed, the constructor will ask for the size, which is how large the two-dimensional array is. The current test supports up to 125.
2. Before buying a pixel, first query the current price of the pixel you want to buy, call the “queryPixel” method, enter the pixel coordinates, and return the pixel information.
3. The "buy" method is used to purchase a pixel, enter the coordinates of the pixel, and the color to be set (rgb format). The value in the advanced option is the nuls you used to purchase the pixel.
4. The "info" method is used for current game information, including whether the game is terminated, the prize pool balance, and the last buyer.
4. The "tryStop" method is used to get the bonus, which anyone can call, but the bonus will only be sent to the last buyer.

Some code logic
1. After the game starts, if there are no pixels purchased in 8640 blocks, then the prize pool will be awarded to the last person who purchased the pixels.
2. The initial price per pixel is 1 nuls. After each transaction, the pixel price increases by 10% and returns to the original holder of the pixel 80% of the nuls.
    A purchase pixel (1,1), spend 1 nuls, B purchase pixel (1,1), it costs 1.1 nuls, the contract automatically takes out 1.1*0.8=0.88 nuls from 1.1 nuls and returns it to A
3. Everyone can follow the information returned by the "info" method in real time, and find that the winner can call the "tryStop" method to retrieve the bonus.

# pixel-master

这是一个简单的类似于eos pixel master的智能合约。
合约一共有3个方法，分别是

    void buy(Short x, Short y, Short red, Short blue, Short yellow);
    
    PixelEntity queryPixel(Short x, Short y);
    
    String info();
    
    boolean tryStop();

以下介绍函数用法
1. 合约部署时，构造函数会要求输入size，即构建多大的二维数组，目前测试最多支持125，后续可以想办法优化
2. 购买像素前，首先查询你要购买的像素当前售价，调用“queryPixel”方法，输入像素坐标，会返回像素信息
3. “buy”方法用来购买一个像素，输入像素的坐标值，以及要设置的颜色(rgb格式)，高级选项中的value即你用来购买像素的nuls
4. "info"方法用来当前游戏信息，包括游戏是否终止、奖池余额、最后一个购买者
4. "tryStop"方法用来获取奖金，任何人都可以调用，只是奖金只会发给最后一个购买者

一些代码逻辑
1. 游戏开始后，如果连续8640个区块都没有购买像素，那么奖池奖金会奖励给最后一个购买像素的人
2. 每个像素初始价格为1个nuls，每次交易后，像素价格上涨10%，并返还给像素原持有者80%的nuls
    A购买像素(1,1)，花了1个nuls，B购买像素(1,1)，要花费1.1个nuls，合约自动从1.1个nuls中拿出1.1*0.8=0.88个nuls返还给A
3. 所有人都可以实时关注"info"方法返回的信息，发现中奖者是自己就可以调用"tryStop"方法取回奖金

