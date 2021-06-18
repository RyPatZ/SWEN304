Select a.Security ,a.RobberyCount,(a.Sum/a.RobberyCount) AS AverageAmount
From (Select b.Security,Count(r.*) AS RobberyCount,Sum(r.Amount)
From Robberies r Natural JOIN Banks b
Group By b.Security) a
