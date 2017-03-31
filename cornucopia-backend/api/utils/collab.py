#SampleSet with movies
movies={'Marcel Caraciolo': {'Lady in the Water': 2.5, 'Snakes on a Plane': 3.5,
 'Just My Luck': 3.0, 'Superman Returns': 3.5, 'You, Me and Dupree': 2.5,
 'The Night Listener': 3.0},
'Luciana Nunes': {'Lady in the Water': 3.0, 'Snakes on a Plane': 3.5,
 'Just My Luck': 1.5, 'Superman Returns': 5.0, 'The Night Listener': 3.0,
 'You, Me and Dupree': 3.5},
'Leopoldo Pires': {'Lady in the Water': 2.5, 'Snakes on a Plane': 3.0,
 'Superman Returns': 3.5, 'The Night Listener': 4.0},
'Lorena Abreu': {'Snakes on a Plane': 3.5, 'Just My Luck': 3.0,
 'The Night Listener': 4.5, 'Superman Returns': 4.0,
 'You, Me and Dupree': 2.5},
'Steve Gates': {'Lady in the Water': 3.0, 'Snakes on a Plane': 4.0,
 'Just My Luck': 2.0, 'Superman Returns': 3.0, 'The Night Listener': 3.0,
 'You, Me and Dupree': 2.0},
'Sheldom': {'Lady in the Water': 3.0, 'Snakes on a Plane': 4.0,
 'The Night Listener': 3.0, 'Superman Returns': 5.0, 'You, Me and Dupree': 3.5},
'Penny Frewman': {'Snakes on a Plane':4.5,'You, Me and Dupree':1.0,'Superman Returns':4.0}}

from api.models import Favorite
from math import sqrt


class CollabRecommender(object):

    def __init__(self, data):
        self.data = data

    def _sim_pearson(self, p1, p2):
        """Returns the Pearson correlation coefficient for p1 and p2"""
        #Get the list of mutually rated items
        si = {}
        for item in self.data[p1]:
            if item in self.data[p2]:
                si[item] = 1

        #if they are no rating in common, return 0
        if len(si) == 0:
            return 0

        #sum calculations
        n = len(si)

        #sum of all preferences
        sum1 = sum([self.data[p1][it] for it in si])
        sum2 = sum([self.data[p2][it] for it in si])

        #Sum of the squares
        sum1Sq = sum([pow(self.data[p1][it],2) for it in si])
        sum2Sq = sum([pow(self.data[p2][it],2) for it in si])

        #Sum of the products
        pSum = sum([self.data[p1][it] * self.data[p2][it] for it in si])

        #Calculate r (Pearson score)
        num = pSum - (sum1 * sum2/n)
        den = sqrt((sum1Sq - pow(sum1,2)/n) * (sum2Sq - pow(sum2,2)/n))
        if den == 0:
            return 0

        r = num/den

        return r

    def __call__(self, person):
        """ Gets recommendations for a person by using a weighted average
        of every other user's rankings"""
        totals = {}
        simSums = {}

        for other in self.data:
            #don't compare me to myself
            if other == person:
                continue
            sim = self._sim_person(person, other)

            #ignore scores of zero or lower
            if sim <= 0:
                continue
            for item in self.data[other]:
                #only score books i haven't seen yet
                if item not in self.data[person] or self.data[person][item] == 0:
                    #Similarity * score
                    totals.setdefault(item,0)
                    totals[item] += self.data[other][item] * sim
                    #Sum of similarities
                    simSums.setdefault(item,0)
                    simSums[item] += sim

        #Create the normalized list
        rankings = [(total/simSums[item],item) for item,total in totals.items()]

        #Return the sorted list
        rankings.sort()
        rankings.reverse()
        return rankings

def initialize_collab_filter():
    raw = Favorite.objects.filter(deleted=False).values_list('user', 'recipe')
    data = {}
    for user, recipe in raw:
        if user not in data:
            data[user] = []
        data[user].append({recipe: 1.0})
    return CollabRecommender(data)
