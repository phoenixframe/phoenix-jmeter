# phoenix-jmeter
phoenixframework平台的一个模块，对jmeter的深度定制，用于对web性能测试。
<br>
这个定制版的jmeter，只支持No-GUI方式执行，并且在执行过程中可以动态获取已启动的线程，处于等待的线程，已经停止的线程，<br>
并且执行完成后可以获取到执行的结果数据，如tps，success，fail等。<br>
<br>
获取方法：<br>
活动线程：JMeterContextService.getThreadCounts().activeThreads<br>
已停止线程：JMeterContextService.getThreadCounts().finishedThreads<br>
实时打印每个线程的执行结果：FlushQueue.getInstance().queueString();<br>
是否在运行：StateListener.isRunning();<br>
设置的总线程数：JMeterContextService.getNumberOfThreads();<br>
已启动的线程数：JMeterContextService.getThreadCounts().startedThreads<br>
测试启动时间：StateListener.getStartTime()<br>
测试结束时间：StateListener.getEndTime()<br>
性能测试结果：Summariser.getSummary()<br>
分机性能数据监控：StateListener.getSlaveMetrics()<br>
测试数据统计：ResultCollector.getRESULT_CAL()<br>
