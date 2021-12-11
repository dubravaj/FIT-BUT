class Equation 
  def self.solve_quadratic(a, b, c)
    diskriminant = b*b-4*a*c

    if a == 0
      if b == 0
         return nil
      else
          x1=(-c)/b.to_f 
      end
    elsif diskriminant > 0
      x1=(-b+Math.sqrt(diskriminant))/(2*a)
      x2=(-b-Math.sqrt(diskriminant))/(2*a)      
    elsif diskriminant == 0
      x1=(-b)/(2*a).to_f
    else
      return nil
    end
   
      if x1!=nil
        if x2!=nil
          return [x1,x2]
        else
          return [x1]
        end
      end

  end  
end
    