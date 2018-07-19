# aonet
* BIO (最为原始的阻塞IO,网络通讯过程中，效率低下)
* 伪异步IO(1，为每个请求分配一条线程处理，容易导致资源消耗完，2，采用线程池的方式处理每一条请求),其本质上还是阻塞IO，当由于网络原因，或者接收处理较慢而导致的，不能及时读取数据时，就会导致阻塞直到IO异常，或者能整成写入
E.g:TCP/IP 写入数据过程中，接收端不能及时消费就会导致windows size 不断减少直到为0，除非window size大于0或者IO异常，否则一直阻塞
* NIO 非阻塞IO，多路复用器selector轮询注册在其上的channel ,,, channel双工通道 
(selector JDK1.7采用epoll()(JDK1.7之前采用select/poll) ,epoll没有链接句柄数的限制,只受限于操作系统做大句柄数，或者对单个进程的句柄数，以为着selector线程可以处理成千上万个客户端链接)
* 自己写NIO难度大，不好维护,重连，闪退，半包读写，缓存失败，异常码流等原因处理难度大，建议netty
* netty即可作为client，也可以作为service，同时支持UDP和异步文件传输
* HTTP+FileServer
* HTTP+XML (HTTP+GSON)



