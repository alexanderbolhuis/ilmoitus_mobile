//
//  Declaration.h
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 24-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Declaration : NSObject
@property (nonatomic) float amount;
@property (strong, nonatomic) NSString *status;
@property (strong, nonatomic) NSString *createdAt;
@property (strong, nonatomic) NSMutableArray *attachments;
@end
