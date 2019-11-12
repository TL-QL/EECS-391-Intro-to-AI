from pgmpy.models import BayesianModel
from pgmpy.factors.discrete import TabularCPD
from pgmpy.inference import VariableElimination

# Exercise 1
print("Exercise 1. Noisy Fuel Guage")

# Defining the model structure. 
model = BayesianModel([('B', 'G'), ('F', 'G')])

# Defining individual CPDs.
cpd_b = TabularCPD(variable='B', variable_card=2, values=[[0.2, 0.8]], state_names={'B': ['Dead', 'Charged']})
cpd_f = TabularCPD(variable='F', variable_card=2, values=[[0.1, 0.9]], state_names={'F':['Empty', 'Full']})

cpd_g = TabularCPD(variable='G', variable_card=2, 
                   values=[[0.9, 0.9, 0.8, 0.1],
                           [0.1, 0.1, 0.2, 0.9]],
                  evidence=['F', 'B'],
                  evidence_card=[2, 2],
                  state_names={'G': ['ReadEmpty', 'ReadFull'], 'F': ['Empty', 'Full'], 'B' : ['Dead', 'Charged']})

# Associating the CPDs with the network
model.add_cpds(cpd_b, cpd_f, cpd_g)

# check_model checks for the network structure and CPDs and verifies that the CPDs are correctly 
# defined and sum to 1.
model.check_model()

infer = VariableElimination(model)

# Exercise 1 c)
print("E1 C: P(F = 0| G = 0) = ")
print(infer.query(['F'], evidence={'G': 'ReadEmpty'}))

# Exercise 1 d)1)
print("E1 D1: P(F) = ")
g_dist = infer.query(['F'])
print(g_dist)

# Exercise 1 d)2)
print("E1 D2: P(F| G = 0) = ")
print(infer.query(['F'], evidence={'G': 'ReadEmpty'}))

# Exercise 2 d)3)
print("E1 D3: P(F | B = 0, G = 0) = ")
print(infer.query(['F'], evidence={'G': 'ReadEmpty', 'B': 'Dead'}))


# Exercise 2
print("********************************************************************")
print("Exercise 2. Wet Grass")

# Defining the model structure. 
model2 = BayesianModel([('R', 'G'), ('S', 'G')])

# Defining individual CPDs.
cpd_r = TabularCPD(variable='R', variable_card=2, values=[[0.8, 0.2]], state_names={'R': ['NotRain', 'Rain']})
cpd_s = TabularCPD(variable='S', variable_card=2, values=[[0.9, 0.1]], state_names={'S':['Off', 'LeftOn']})

cpd_g2 = TabularCPD(variable='G', variable_card=2, 
                   values=[[1, 0.95, 0, 0],
                           [0, 0.05, 1, 1]],
                  evidence=['R', 'S'],
                  evidence_card=[2, 2],
                  state_names={'G': ['Dry', 'Wet'], 'R': ['NotRain', 'Rain'], 'S' : ['Off', 'LeftOn']})

# Associating the CPDs with the network
model2.add_cpds(cpd_r, cpd_s, cpd_g2)

# check_model checks for the network structure and CPDs and verifies that the CPDs are correctly 
# defined and sum to 1.
model2.check_model()

infer2 = VariableElimination(model2)

# Exercise 2 d)
# Answer to "Was it due to an overnight rain? Or that last night she forgot to turn off her sprinkler?"
print("E2 D:")
print(infer2.query(variables=['R'], evidence={'G': 'Wet'}))
print(infer2.query(variables=['S'], evidence={'G': 'Wet'}))
print(infer2.map_query(variables=['R','S'], evidence={'G': 'Wet'}))


# With additional information about John's Grass
# Defining the model structure. 
model3 = BayesianModel([('R', 'G'), ('S', 'G'), ('R', 'J')])

# Defining individual CPDs.
# Only one new CPD needed.
cpd_j = TabularCPD(variable='J', variable_card=2, 
                   values=[[0.85, 0],
                           [0.15, 1]],
                  evidence=['R'],
                  evidence_card=[2],
                  state_names={'J': ['JDry', 'JWet'], 'R': ['NotRain', 'Rain']})

# Associating the CPDs with the network
model3.add_cpds(cpd_r, cpd_s, cpd_g2, cpd_j)

# check_model checks for the network structure and CPDs and verifies that the CPDs are correctly 
# defined and sum to 1.
model3.check_model()

# Exercise 2 f)
infer3 = VariableElimination(model3)
print("E2 f: P(S = 1 | G = 1, J = 1) = ")
print(infer3.query(['S'], evidence={'G': 'Wet', 'J': 'JWet'}))