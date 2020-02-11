# P3
# EECS 391
# Qiwen Luo
# qxl216

import pandas as pd 
import matplotlib.pyplot as plt
from matplotlib import cm
import numpy as np
import math
from mpl_toolkits import mplot3d
from mpl_toolkits.mplot3d import Axes3D
import random

import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import torch.utils.data as data

# Q1
dataframe = pd.read_csv('./irisdata.csv')
two_class = dataframe[dataframe['species']!= 'setosa']
two_class.loc[two_class['species'] ==  'versicolor', 'species'] = 0
two_class.loc[two_class['species'] == 'virginica', 'species'] = 1
in_vec = two_class[['petal_length', 'petal_width']]
out_vec = two_class['species']
w1 = [1.88, 20.56]
bias = -38.994

def plot_scatter():
    plt.scatter(in_vec.values[:,0], in_vec.values[:,1], c=out_vec.values) 

plot_scatter()
plt.colorbar()
plt.xlabel('Petal Length')
plt.ylabel('Petal Width')
plt.title('Inspect irisdata.csv')
plt.show()

def mysigmoid(z):
    result = 1.0/(1+math.exp(-z))
    return result
def output_log_single(w, b, in_petal_length, in_petal_width):
    y = mysigmoid(w[0]*in_petal_length+w[1]*in_petal_width+b)
    return y
def output_log(w, b, in_petal_length, in_petal_width):
    y = []
    for i in range(len(in_petal_length)):
        temp = output_log_single(w, b, in_petal_length.iloc[i], in_petal_width.iloc[i])
        y.append(temp)
    return y

def decision_boundary(w, b, myLabel):
    plt.xlim(2.0,7.0)
    x = np.asarray([2.0, 7.0])
    plt.plot(x,(-b-w[0]*x)/w[1], label = myLabel)

decision_boundary(w1, bias, "Line")
plt.scatter(in_vec.values[:,0], in_vec.values[:,1], c=out_vec.values) 
plt.xlabel('Petal Length')
plt.ylabel('Petal Width')
plt.title('Decision Boundary')
plt.legend() 
plt.show()

def surface_plot(w, b):
    fig = plt.figure()
    ax = Axes3D(fig)
    x1 = np.arange(1, 7, 0.2)
    x2 = np.arange(0, 3, 0.2)
    X1, X2 = np.meshgrid(x1, x2)
    y = np.array([output_log_single(w, b, x1, x2) for x1, x2 in zip(np.ravel(X1), np.ravel(X2))])
    Y = y.reshape(X1.shape)
    #ax.view_init(elev=20,azim=15)
    ax.plot_wireframe(X1, X2, Y, rstride=1, cstride=1, color = 'k')
    ax.set_xlabel('Petal Length')
    ax.set_ylabel('Petal Width')
    ax.set_zlabel('Output')
    ax.set_title('Surface Plot')
    plt.show()

# ww = [1.8, 4]
# bb = -15.3
surface_plot(w1, bias)

def round_output(input):
    if input >= 0.5:
        return 1
    else:
        return 0

print()
test1 = [6, 2.5]
print("Unambigious 3rd class")
print(round_output(output_log_single(w1, bias, test1[0], test1[1])))

test2 = [3.3, 1]
print("Unambigious 2rd class")
print(round_output(output_log_single(w1, bias, test2[0], test2[1])))

test3 = [4.9, 1.5]
print("Ambigious 2rd class")
print(round_output(output_log_single(w1, bias, test1[0], test1[1])))

test3 = [5.6, 1.4]
print("Ambigious 3rd class")
print(round_output(output_log_single(w1, bias, test1[0], test1[1])))
print()

fig, axs = plt.subplots(2)
fig.suptitle('Before and After implementing classifier')
axs[0].scatter(in_vec.values[:,0], in_vec.values[:,1], c=out_vec.values) 
temp = output_log(w1, bias, two_class['petal_length'], two_class['petal_width'])
for i in range(len(temp)):
    temp[i] = round_output(temp[i])
axs[1].scatter(in_vec.values[:,0], in_vec.values[:,1], c=pd.Series(temp).values) 
x = np.asarray([2.0, 7.0])
axs[0].set_xlim([2.0,7.0])
axs[0].plot(x,(-bias-w1[0]*x)/w1[1])
axs[1].set_xlim([2.0,7.0])
axs[1].plot(x,(-bias-w1[0]*x)/w1[1])
plt.show()


# Q2
def cal_mean_square(petal_lengths, petal_widths, species, w, b, pattern):
    new_y = output_log(w, b, petal_lengths, petal_widths)
    result = 0
    for i in range(len(petal_lengths)):
        result += (new_y[i] - species.iloc[i])**2
    return result/len(petal_lengths)

print("w1=%f w2=%f bias=%f"%(w1[0],w1[1],bias))
print("Mean-squared error of the iris data")
print(cal_mean_square(two_class['petal_length'], two_class['petal_width'], two_class['species'], w1, bias, ["versicolor", "virginica"]))
print()

w2 = [2.87, 27.56]
bias2 = -48.994
print("w1=%f w2=%f bias=%f"%(w2[0],w2[1],bias2))
print("Mean-squared error of the iris data")
print(cal_mean_square(two_class['petal_length'], two_class['petal_width'], two_class['species'], w2, bias2, ["versicolor", "virginica"]))
print()

w3 = [3.67, 34.56]
bias3 = -52.78
print("w1=%f w2=%f bias=%f"%(w3[0],w3[1],bias3))
print("Mean-squared error of the iris data")
print(cal_mean_square(two_class['petal_length'], two_class['petal_width'], two_class['species'], w3, bias3, ["versicolor", "virginica"]))
print()

def cal_summed_grad(w, b):
    x1 = two_class['petal_length']
    x2 = two_class['petal_width']
    y = two_class['species']
    result = [0,0]
    for i in range(len(two_class['petal_length'])):
        temp = math.exp(-(w[0]*x1.iloc[i]+w[1]*x2.iloc[i]+b))
        result[0] += (2*(1.0/(1+temp)-y.iloc[i])*(-1.0/(1+temp)**2)*temp*(-x1.iloc[i]))
        result[1] += (2*(1.0/(1+temp)-y.iloc[i])*(-1.0/(1+temp)**2)*temp*(-x2.iloc[i]))
    return result

print("Summed gradient")
print(cal_summed_grad(w1, bias))
print()


# Q3
def learning_curve(iter, mean_squares):
    x = list(range(1, iter+1))
    plt.plot(x,mean_squares)

def optimize_decision_boundary(w, b):
    iter = 200
    mean_squares = []
    previous = [w[0],w[1]]
    current = [w[0], w[1]]
    plot_scatter()
    decision_boundary(current, b, "Initial")
    eps = 0.5/len(two_class['petal_length'])
    for i in range(iter):
        current[0] = previous[0] - eps * cal_summed_grad(previous, b)[0]
        current[1] = previous[1] - eps * cal_summed_grad(previous, b)[1]
        previous[0] = current[0]
        previous[1] = current[1]
        mean_squares.append(cal_mean_square(two_class['petal_length'], two_class['petal_width'], two_class['species'],current, b, ["versicolor", "virginica"]))
        if i == round(iter/3, 0):
            decision_boundary(current, b, "Middle")
    decision_boundary(current, b, "Final")
    plt.xlabel('Petal Length')
    plt.ylabel('Petal Width')
    plt.title('Decision Boundary')
    plt.legend() 
    plt.legend( loc = 'upper right')
    plt.show()
    learning_curve(iter, mean_squares)
    return current

print("Initialized w")
print(w1)
print("Optimized w")
print(optimize_decision_boundary(w1, bias))
print()

plt.xlabel('Number of Weight Updates')
plt.ylabel('Squared Error Per Example')
plt.title('Learning Curve')
plt.show()

print("Output 1")
w4 = [random.uniform(1.1,5), random.uniform(15.78,24.88)]
print("Initial w")
print(w4)
print("Optimized w")
print(optimize_decision_boundary(w4, bias))
plt.xlabel('Number of Weight Updates')
plt.ylabel('Squared Error Per Example')
plt.title('Learning Curve')
plt.show()
print()

print("Output 2")
w5 = [random.uniform(1.1,5), random.uniform(15.78,24.88)]
print("Initial w")
print(w5)
print("Optimized w")
print(optimize_decision_boundary(w5, bias))
plt.xlabel('Number of Weight Updates')
plt.ylabel('Squared Error Per Example')
plt.title('Learning Curve')
plt.show()
print()


# Extra Credit
num_in = 4 
num_out = 1 
four_class1 = dataframe
four_class1.loc[four_class1['species'] == 'virginica', 'species'] = 1
four_class1.loc[four_class1['species'] ==  'versicolor', 'species'] = 0.5
four_class1.loc[four_class1['species'] ==  'setosa', 'species'] = 0
in_vec1 = four_class1[['sepal_length', 'sepal_width', 'petal_length', 'petal_width']]
out_vec1 = four_class1['species']

class Network(nn.Module): 
    def __init__(self):
        super(Network, self).__init__()
        self.fullyconnected1 = nn.Linear(num_in,num_out)
    def forward(self, x):
        x = self.fullyconnected1(x) 
        x = F.sigmoid(x)
        return x

model = Network()
criterion = nn.MSELoss()
optimizer1 = torch.optim.SGD(model.parameters(), lr=0.01)

num_epochs = 1000
num_examples1 = four_class1.shape[0]
model.train()
for epoch in range(num_epochs):
    for idx in range(num_examples1):
        attributes1 = torch.tensor(in_vec1.iloc[idx].values, dtype=torch.float) 
        label1 = torch.tensor(out_vec1.iloc[idx], dtype=torch.float)
        optimizer1.zero_grad()
        output1 = model(attributes1)
        loss1 = criterion(output1, label1)
        loss1.backward()
        optimizer1.step() 
 
    if(epoch % 100 == 0):
        print('Epoch1: {} | Loss1: {:.6f}'.format(epoch, loss1.item()))

model.eval()
count = 0
pred1 = torch.zeros(out_vec1.shape)
for idx in range(num_examples1):
    attributes1 = torch.tensor(in_vec1.iloc[idx].values, dtype=torch.float)
    label1 = torch.tensor(out_vec1.iloc[idx], dtype=torch.float)
    pred1[idx] = (model(attributes1)*2).round()/2
    if pred1[idx] == out_vec1.iloc[idx]:
        count += 1


print('Correct classifications: {}/{}'.format(count,len(out_vec1)))