import threading

class LoopThread(threading._Timer):
    def run(self):
        while True:
            self.finished.wait(self.interval)
            if not self.finished.is_set():
                self.function(*self.args, **self.kwargs)
            else:
                self.finished.set()
                break

def ff():
    global i
    i = i + 1
    print "hh" + str(i)
def qq(a):
    a[0].cancel()
    print a[1]
global i
i = 0
timer = LoopThread(1, ff)
sss = threading.Timer(3, qq, ([timer,'test'],))
timer.start()
sss.start()