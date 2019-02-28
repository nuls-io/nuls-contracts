# red-envelope

This is a smart contract based on NULS's NVM that implements the function of red envelope.
There are 6 functions to invoke

    @Payable
    void newRedEnvelope(@Required Byte parts, @Required Boolean random, String remark);

    Nuls snatchRedEnvelope(@Required Long id);

    void retrieveRedEnvelope(@Required Long id);

    @View
    RedEnvelopeEntity detailInfo(@Required Long id);

    @View
    Boolean availableInfo(@Required Long id);

    @View
    List<String> allInfo();

The following describes function usage
1. "newRedEnvelope" is used to send red envelopes, "parts" stands for red envelopes with several copies, "random" stands for random red envelopes, and remark is auspicious. 
    When calling the contract, the value in the advanced option is the amount of red envelopes you want to send, and the ID of the red envelopes will be returned.
2. "snatchRedEnvelope" is used to snatch the red envelope, enter the ID of the red envelope you want to snatch, and the call will return how many NULS you snatch.
3. "AllInfo" is used to find which red envelopes have been sent so far. The return value is a list in the format of "id:available".
4. "DetailInfo" queries the details of the red envelope through the red envelope ID, 
    including the sponsor, the total amount of the red envelope, the remaining amount of the red envelope, details of grabbing the red envelope, and so on.
    The red envelope must be robbed over before you can view the details.
5. "availableInfo" Query whether a red envelope can be robbed by the red envelope ID. By default, the red envelope is sent after 8640 (calculated by 10 seconds to generate a block, exactly 24 hours).
    The height of the block and the balance will be returned to the owner.
6. After the red envelope owner has confirmed the red envelope is timeout via the "availableInfo" method, 
    he or she can call the "retrieveRedEnvelope" method to retrieve the balance, 
    and only the red envelope's initiator can call this method successfully.

Here are some configuration and logical information
1. There are upper and lower limits for the amount of red envelopes, with a minimum of 0.01nuls and a maximum of 200nuls.
2. The total amount of red packets / red envelope's parts should not be less than 0.01nuls.
3. After the red envelope is sent, the height of more than 1,000 blocks and the balance can not be grabbed, and the red envelope's owner can retrieve the red envelope balance by calling a specific contract method.
4. One account can only grab the same red envelope once
5. When there is only one part of the red envelope, the amount is not calculated and the balance is taken directly.
6. Random red envelope allocation algorithm
    When grabbing the red envelope, assume that there are N red envelopes left, the red envelope balance is M, and the minimum value of the red packet is MIN=0.01nuls
    Then grab the red envelope amount expected expect = M/N;
    Red envelope amount range (MIN, 2*expect)
    Considering the amount remaining in the red envelope must be > (number of remaining shares * MIN)

# 红包

这是一个实现了红包功能的智能合约。
合约一共有6个方法，分别是

    @Payable
    void newRedEnvelope(@Required Byte parts, @Required Boolean random, String remark);

    Nuls snatchRedEnvelope(@Required Long id);

    void retrieveRedEnvelope(@Required Long id);

    @View
    RedEnvelopeEntity detailInfo(@Required Long id);

    @View
    Boolean availableInfo(@Required Long id);

    @View
    List<String> allInfo();

以下介绍函数用法
1. newRedEnvelope用来发红包，parts代表红包分几份，random代表是否为随机红包，remark为吉祥话，调用合约时高级选项中的value就是你要发送的红包金额，调用成功会返回红包的ID。
2. snatchRedEnvelope用来抢红包，输入你要抢的红包ID，调用成功后会返回你抢了多少个NULS。
3. allInfo用来查询目前已经发送了哪些红包，返回值为列表，格式为"id:available"，可以通过调用此方法获取当前有哪些红包可以抢。
4. detailInfo通过红包ID查询红包详细信息，包括发起人、红包总金额、红包剩余金额、抢红包明细等信息，红包被抢完才可以查看红包详情。
5. availableInfo通过红包ID查询某红包是否可抢，默认设置红包发出后经过8640(按10秒生成一个区块计算，正好24小时)个区块高度还有余额，就会返还给所有者
6. 红包所有者通过availableInfo方法查询到自己发出的红包超时后，可以调用retrieveRedEnvelope方法取回余额，只有红包发起者可以调用成功此方法。

以下介绍一些配置、逻辑信息
1. 发送红包有金额上下限，最小为0.01nuls，最大为200nuls。
2. 红包总金额/红包份数不能小于0.01nuls。
3. 红包发出后超过1000个区块高度还有余额，就不可以被抢了，红包所有者可以通过调用特定合约方法取回红包余额。
4. 同一个红包每个账户只能抢一次
5. 红包只剩一份时，抢到的金额不通过计算得出，直接取余额。
6. 随机红包分配算法
    每次抢红包时，假定红包还剩N份，红包余额为M，可抢红包最小值为MIN=0.01nuls
    则抢到红包金额期望expect = M / N;
    红包金额波动区间（MIN，2*expect），并且红包剩余金额必须>(剩余份数*MIN)

