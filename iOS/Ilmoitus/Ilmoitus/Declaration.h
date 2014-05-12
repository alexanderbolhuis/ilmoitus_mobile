//
//  Declaration.h
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 24-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Declaration : NSObject
// id
@property (strong, nonatomic) NSString *className;
@property (strong, nonatomic) NSString *status;
@property (strong, nonatomic) NSString *createdAt;
@property (nonatomic) int createdBy;
@property (strong, nonatomic) NSArray *assignedTo;
@property (strong, nonatomic) NSString *comment;
@property (nonatomic) float itemsTotalPrice;
@property (nonatomic) int itemsCount;
@property (strong, nonatomic) NSArray *lines;
@end
