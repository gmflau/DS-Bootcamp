import random
import uuid
import datetime

from sys import argv
script, filename = argv

target = open(filename, 'w')

vendor = ['Samsung', 'Philips', 'GE', 'Honeywell']
maint_history = ['Repalced IC30486. ' , 'Serviced without issues. ', 'Need service update in 10 days. ', 'Repalced IC666789. ']
sensor_type = ['temperature', 'movement', 'humidity']
numHive = 4  
wing = ['North', 'South', 'East', 'West']
min_floor = 17
max_floor = 45
geoLocation = [['37.7484' , '-122.4156'], ['40.7143', '-74.006'], ['45.536951', '-122.649971'], ['37.238754' , '-76.509674']]

for x in range(1, numHive+1):
   for y in range(min_floor, max_floor):
      numSensors = random.randint(2, 5)
      for z in range(0, numSensors):
         target.write(vendor[random.randint(0,len(vendor)-1)] + ',')   
         target.write(str(uuid.uuid1()) + ',')  
         target.write((datetime.datetime.now() - datetime.timedelta(random.randint(12*30,24*30))).strftime("%Y-%m-%d") + ',')
         target.write((datetime.datetime.now() - datetime.timedelta(random.randint(60,6*30))).strftime("%Y-%m-%d") + ',')
         target.write(maint_history[random.randint(0,3)] + ',')
         target.write((datetime.datetime.now() + datetime.timedelta(random.randint(24*30,60*30))).strftime("%Y-%m-%d") + ',')
         target.write(sensor_type[random.randint(0,2)] + ',')
         target.write(str(y) + ',')
         target.write(wing[random.randint(0,3)] + ',')
         target.write(str(x) + ',')
         target.write(geoLocation[x-1][0] + ',')
         target.write(geoLocation[x-1][1])
         target.write("\n")

target.close()
