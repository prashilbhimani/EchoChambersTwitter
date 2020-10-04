import math

def print_dict(b):
	l=[]
	for community  in b.keys():
		if len(b[community].keys())>0:
			print(community)
			for hashtags in b[community].keys():
				if int(community)>int(hashtags):
					l.append([community,hashtags,b[community][hashtags]])
				#print("\t"+hashtags+": "+str(b[community][hashtags]))
	print(l)


def actual_print_dict(b):
	l=[]
	for community  in b.keys():
		if len(b[community].keys())>0:
			print(community)
			for hashtags in b[community].keys():
				#if int(community)>int(hashtags):
				#	l.append([community,hashtags,b[community][hashtags]])
				print("\t"+hashtags+": "+str(b[community][hashtags]))
	#print(l)
def counter_cosine_similarity(c1, c2):
	if len(c1.keys())==0 or len(c2.keys())==0:
		return 0
	terms = set(c1).union(c2)
	dotprod = sum(c1.get(k, 0) * c2.get(k, 0) for k in terms)
	magA = math.sqrt(sum(c1.get(k, 0)**2 for k in terms))
	magB = math.sqrt(sum(c2.get(k, 0)**2 for k in terms))
	return dotprod / (magA * magB)


f=open("top20.csv")
for line in f:
	break
b={}
filter=["climatechange","fiji","thursdaythoughts","wednesdaywisdom","facts","earth", "breaking","tbt","flake" ,"tuesdaythoughts","fridayfeeling","bangladesh","art","love","sarcasm", "jobs","world","food","parisclimateagreement","china","srilanka", "florida","africa","denver","india","actonclimate","parisagreement","climate","parisaccord","parisclimateaccord","globalwarming","parisclimatedeal","parisagreeement", "trump", "newyork", "paris", "boston", "chicago", "sea", "auspol", "cdnpoli", "bcpoli", "qldpol"]
for line in f:
	hashtag,community,count=line.split('\n')[0].split(',')
	if community not in b:
		b[community]={}
	if int(count)>50 and hashtag not in  filter:
		b[community][hashtag]=int(count)

keys=b.keys()

for community in keys:
	total =0
	for tags in b[community]:
		total=total+b[community][tags]
	for tags in b[community]:
		b[community][tags]=float(b[community][tags])/total
actual_print_dict(b)

similarity={}

for i in range(len(keys)):
	similarity[keys[i]]={}
	for j in range(len(keys)):
		s=round(counter_cosine_similarity(b[keys[i]],b[keys[j]]),3)
		if s>0.4 and i is not j:
			similarity[keys[i]][keys[j]]=s
print_dict(similarity)
print(keys)