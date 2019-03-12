import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
import matplotlib.pyplot as plt
MAX_X = 800
MAX_Y = 600
memory = 32
class MouseRNN():

	def __init__(self, memory = 100,inputs = 320, outputs = 2 ,hidden = 624,layers = 5, scope = 'soundrnn'):
		self.hidden = hidden
		self.input = inputs
		self.memory = memory
		self.outputs = outputs
		self.layers = layers
		with tf.variable_scope(scope):
			self.init_model()

	def init_model(self):
		self.x = tf.placeholder(tf.float32, [None,self.memory ,self.input])
		self.y = tf.placeholder(tf.float32, [None, self.outputs])

		self.w = tf.get_variable('w', [self.input, self.hidden], dtype = tf.float32,initializer= tf.initializers.random_uniform())
		self.b = tf.get_variable('b', [self.hidden], dtype = tf.float32, initializer =tf.initializers.zeros())

		#xx = tf.transpose(self.x, [0,2,1])
		xx = tf.reshape(self.x, [-1, self.input])
		xx = tf.nn.softplus(tf.matmul(xx, self.w) + self.b)
		xx = tf.reshape(xx, [-1, self.memory, self.hidden])
		#xx = tf.transpose(xx, [0,2,1])
		xx = tf.unstack(xx, self.memory, 1)

		cell = tf.contrib.rnn.BasicLSTMCell(self.hidden,activation = tf.nn.tanh)

		cell = tf.contrib.rnn.MultiRNNCell([cell]*self.layers)
		outputs, states = tf.contrib.rnn.static_rnn(cell, xx, dtype = tf.float32)

		self.w2 = tf.get_variable('w2', [self.hidden, self.outputs], dtype = tf.float32,initializer= tf.initializers.random_uniform())
		self.b2 = tf.get_variable('b2', [self.outputs], dtype = tf.float32, initializer =tf.initializers.zeros())

		out = tf.matmul(outputs[-1], self.w2) + self.b2
		self.out = tf.nn.tanh(out)
		#self.loss = tf.reduce_mean(-y_l*tf.log(out_l) - (1-y_l)*tf.log(1-out_l))
		self.loss = tf.reduce_mean(tf.pow(self.out - self.y, 2))

def readData():
    endOfRead = False;
    data = []
    index = 1
    while(not endOfRead):
        try:
            file = open("./records/" + str(index) + ".txt", "r")
            string = file.read()
            paths = []
            for path in string.split("\n"):
                coords = path.split(',')
                pathArr = []
                for i in range(int(len(coords)/2)):
                    pathArr.append([int(coords[2*i]), int(coords[2*i+1])])
                paths.append(np.array(pathArr));

            paths = np.array(paths);
            #data = np.concatenate(data, paths);
            data.append(paths)
            index += 1
        except:
            endOfRead = True;

    return np.concatenate(np.array(data));
def getStartIndexes(data, memory):
	startIndexes = [len(data[0])-memory]
	for i in range(1,len(data)):
		startIndexes.append(startIndexes[-1] + (len(data[i]) - memory))
	return startIndexes;


def makeData(dat):
    maxx = len(dat[0])
    dat2 = []
    minSample = 56
    for i in range(1,len(dat)):
        if(len(dat[i])>=minSample):
            dat2.append(dat[i])
    dat3 = []

    for i in range(len(dat2)):
        xx = []
        for j in range(len(dat2[i])):
            dx = (dat2[i][-1][0] - dat2[i][j][0])/MAX_X
            dy = (dat2[i][-1][1] - dat2[i][j][1])/MAX_Y
            v = np.sqrt((np.power(dat2[i][-1][0] - dat2[i][0][0],2) + np.power(dat2[i][-1][1] - dat2[i][0][1],2)))/ len(dat2[i])
            xx.append(np.array([dx,dy,v]))
        dat3.append(xx)

    dat3 = np.array(dat3)
    x = []
    y = []
    for i in range(len(dat3)):
        for j in range(len(dat3[i])-memory):
            x.append(dat3[i][j:j+memory])
            y.append(dat3[i][j+memory][:2])


    x = np.array(x)
    y = np.array(y)
    x[:,:,2] = x[:,:,2]/np.max(x[:,:,2])

    return x,y
def freerun(sess,net, starting_position, memory, n):
	out = starting_position.tolist()
	for i in range(n):
		output = sess.run(net.out, feed_dict = {net.x:[np.array(out)[-memory:]]})
		out.append(np.array([output[0][0], output[0][1], out[0][2]]))
		print(str(int(output[0][0]*MAX_X)) + " " + str(int(output[0][1]*MAX_Y)))

		if(np.absolute(output[0][0]*MAX_X) <5 and np.absolute(output[0][1]*MAX_Y)<5):
			break;

	return np.array(out)

dat = readData()
xdata,ydata = makeData(dat)
architechture = [256,256,64]

alpha = 0.0001
batch_size = 1024
epochs = 10000
batches = int(len(ydata)/batch_size)
validationX = xdata[-(len(ydata)%batch_size):]
validationy = ydata[-(len(ydata)%batch_size):]
net = MouseRNN(memory = memory, inputs = 3, outputs = 2,hidden = 256, layers = 2, scope = "mousernn")
optimizer = tf.train.AdamOptimizer(alpha).minimize(net.loss)
saver = tf.train.Saver()
saveDirectory = './model/params'
startIndexes = getStartIndexes(dat,memory)
sess = tf.Session()
sess.run(tf.global_variables_initializer())
saver.restore(sess, saveDirectory)
'''
for i in range(epochs):
	for j in range(batches):
		batch_x = xdata[j*batch_size:(j+1)*batch_size]
		batch_y = ydata[j*batch_size:(j+1)*batch_size]
		sess.run(optimizer, feed_dict ={net.x:batch_x, net.y:batch_y})
		if(j%10 == 0):
			print(sess.run(net.loss, feed_dict = {net.x:validationX, net.y:validationy}))
	if(i%30==0):
		saver.save(sess, saveDirectory)
	print('epoch ' + str(i))

'''
index = startIndexes[23]
testrun = ydata[startIndexes[3]:startIndexes[4]] #freerun(sess, net, xdata[index], memory, 50)[memory:]
plt.scatter(testrun[:,0]*MAX_X, testrun[:,1]*MAX_Y)
#plt.scatter(xdata[index][0], xdata[index][1])
plt.show()
